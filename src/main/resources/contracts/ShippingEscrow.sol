// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0 <0.9.0;

// 只声明我们要用的三个函数。Escrow 通过这个接口去调用任何 ERC20 合约。
interface IShippingToken {
    function transfer(address to, uint256 value) external returns (bool);
    function transferFrom(address from, address to, uint256 value) external returns (bool);
    function balanceOf(address account) external view returns (uint256);
}

/// Shipping extension of the group Escrow; legacy contract remains unchanged.
contract ShippingEscrow {
    uint256 public constant SHIPPING_VERSION = 1;
    // -------- 状态机 --------
    // Append Shipped so the original 0..4 values stay stable.
    enum Status { Created, Paid, Completed, Refunded, Disputed, Shipped }

    struct Order {
        address buyer;
        address seller;
        address token;      // 用哪种 ERC20 结算
        uint256 amount;     // 最小单位
        uint256 deadline;   // unix 时间戳，秒
        Status status;
        string description;
    }

    // -------- 存储 --------
    address public arbiter;
    uint256 public orderCount;
    mapping(uint256 => Order) private _orders;

    // -------- 事件 --------
    event OrderCreated(uint256 indexed id, address indexed buyer, address indexed seller, address token, uint256 amount, uint256 deadline);
    event OrderPaid(uint256 indexed id);
    event OrderShipped(uint256 indexed id);
    event OrderCompleted(uint256 indexed id);
    event OrderRefunded(uint256 indexed id);
    event OrderDisputed(uint256 indexed id, address by);
    event OrderResolved(uint256 indexed id, bool sellerWins);

    // -------- 修饰器 --------
    modifier onlyArbiter() {
        require(msg.sender == arbiter, "Escrow: caller is not the arbiter");
        _;
    }

    modifier onlyBuyer(uint256 id) {
        require(msg.sender == _orders[id].buyer, "Escrow: caller is not the buyer");
        _;
    }

    modifier onlySeller(uint256 id) {
        require(msg.sender == _orders[id].seller, "Escrow: caller is not the seller");
        _;
    }

    modifier inStatus(uint256 id, Status s) {
        require(_orders[id].status == s, "Escrow: wrong status");
        _;
    }

    constructor(address _arbiter) {
        require(_arbiter != address(0), "Escrow: arbiter is the zero address");
        arbiter = _arbiter;
    }

    function getOrder(uint256 id) public view returns (Order memory) {
        require(id < orderCount, "Escrow: order does not exist");
        return _orders[id];
    }

    // -------- TODO 1: 创建订单 --------
    function createOrder(address seller, address token, uint256 amount, uint256 deadline, string memory description) public returns (uint256) {
        require(seller != address(0), "Escrow: seller is the zero address");
        require(seller != msg.sender, "Escrow: seller is the buyer");
        require(amount > 0, "Escrow: amount is zero");
        require(deadline > block.timestamp, "Escrow: deadline in the past");

        uint256 id = orderCount;
        _orders[id] = Order({
            buyer: msg.sender,
            seller: seller,
            token: token,
            amount: amount,
            deadline: deadline,
            status: Status.Created,
            description: description
        });
        orderCount++;

        emit OrderCreated(id, msg.sender, seller, token, amount, deadline);
        return id;
    }

    // -------- TODO 2: 买家付款 --------
    // 按指导要求纠正骨架中的状态检查：付款前应为 Created，而不是 Paid。
    function deposit(uint256 id) public onlyBuyer(id) inStatus(id, Status.Created) {
        Order storage o = _orders[id];
        o.status = Status.Paid;
        require(
            IShippingToken(o.token).transferFrom(msg.sender, address(this), o.amount),
            "Escrow: transferFrom failed"
        );
        emit OrderPaid(id);
    }

    // Only the actual seller may ship a paid order before its deadline.
    function markShipped(uint256 id) public onlySeller(id) inStatus(id, Status.Paid) {
        require(block.timestamp <= _orders[id].deadline, "Escrow: shipping deadline passed");
        _orders[id].status = Status.Shipped;
        emit OrderShipped(id);
    }

    // -------- TODO 3: 买家确认收货 --------
    function confirm(uint256 id) public onlyBuyer(id) inStatus(id, Status.Shipped) {
        Order storage o = _orders[id];
        o.status = Status.Completed;
        require(IShippingToken(o.token).transfer(o.seller, o.amount), "Escrow: transfer failed");
        emit OrderCompleted(id);
    }

    // -------- TODO 4: 超期退款 --------
    function refund(uint256 id) public onlyBuyer(id) inStatus(id, Status.Paid) {
        Order storage o = _orders[id];
        require(block.timestamp > o.deadline, "Escrow: deadline not reached");
        o.status = Status.Refunded;
        require(IShippingToken(o.token).transfer(o.buyer, o.amount), "Escrow: transfer failed");
        emit OrderRefunded(id);
    }

    // -------- TODO 5: 发起争议 --------
    function dispute(uint256 id) public {
        Order storage o = _orders[id];
        require(o.status == Status.Paid || o.status == Status.Shipped, "Escrow: wrong status");
        require(msg.sender == o.buyer || msg.sender == o.seller, "Escrow: caller is not the buyer or seller");
        o.status = Status.Disputed;
        emit OrderDisputed(id, msg.sender);
    }

    // -------- TODO 6: 仲裁 --------
    function resolve(uint256 id, bool sellerWins) public onlyArbiter inStatus(id, Status.Disputed) {
        Order storage o = _orders[id];
        if (sellerWins) {
            o.status = Status.Completed;
            require(IShippingToken(o.token).transfer(o.seller, o.amount), "Escrow: transfer failed");
        } else {
            o.status = Status.Refunded;
            require(IShippingToken(o.token).transfer(o.buyer, o.amount), "Escrow: transfer failed");
        }
        emit OrderResolved(id, sellerWins);
    }
}
