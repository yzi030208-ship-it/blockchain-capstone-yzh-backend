// SPDX-License-Identifier: MIT
pragma solidity >=0.8.11 <0.9.0;

contract YZHToken {
    string public name = "YZHToken";
    string public symbol = "YZH202466332566";
    uint8 public decimals = 18;
    uint256 public totalSupply;

    address public owner;
    mapping(address => uint256) private _balances;
    mapping(address => mapping(address => uint256)) private _allowances;

    event Transfer(address indexed from, address indexed to, uint256 value);
    event Approval(address indexed owner, address indexed spender, uint256 value);

    modifier onlyOwner() {
        require(msg.sender == owner, "YZHToken: caller is not the owner");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }

    function allowance(address tokenOwner, address spender)
        public view returns (uint256)
    {
        return _allowances[tokenOwner][spender];
    }
        function transfer(address to, uint256 value) public returns (bool) {
        require(to != address(0), "YZHToken: transfer to zero address");
        require(_balances[msg.sender] >= value, "YZHToken: insufficient balance");

        _balances[msg.sender] -= value;
        _balances[to] += value;

        emit Transfer(msg.sender, to, value);
        return true;
    }
        function approve(address spender, uint256 value) public returns (bool) {
        _allowances[msg.sender][spender] = value;
        emit Approval(msg.sender, spender, value);
        return true;
    }
        function transferFrom(address from, address to, uint256 value)
        public returns (bool)
    {
        require(to != address(0), "YZHToken: transfer to zero address");
        require(_balances[from] >= value, "YZHToken: insufficient balance");
        require(
            _allowances[from][msg.sender] >= value,
            "YZHToken: insufficient allowance"
        );

        _allowances[from][msg.sender] -= value;
        _balances[from] -= value;
        _balances[to] += value;

        emit Transfer(from, to, value);
        return true;
    }
        function mint(address to, uint256 value) public onlyOwner {
        require(to != address(0), "YZHToken: mint to zero address");

        totalSupply += value;
        _balances[to] += value;

        emit Transfer(address(0), to, value);
    }
        function burn(uint256 value) public {
        require(_balances[msg.sender] >= value, "YZHToken: insufficient balance");

        _balances[msg.sender] -= value;
        totalSupply -= value;

        emit Transfer(msg.sender, address(0), value);
    }
}