package com.wetech.demo.web3j.shipping;

import com.wetech.demo.web3j.contracts.escrow.Escrow;
import com.wetech.demo.web3j.contracts.shippingescrow.ShippingEscrow;
import com.wetech.demo.web3j.contracts.yzhtoken.YZHToken;
import com.wetech.demo.web3j.escrow.EscrowEventQueryService;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.wetech.demo.web3j.escrow.EscrowApiErrorHandler;

/** Disposable chain only. Never reads a real wallet key or uses a public RPC. */
@EnabledIfEnvironmentVariable(named = "DHT_LOCAL_RPC", matches = "http://(127\\.0\\.0\\.1|localhost):[0-9]+")
class ShippingEscrowLocalIntegrationTest {
    private static final BigInteger ONE_TOKEN = BigInteger.TEN.pow(18);
    private HttpService transport;
    private Web3j web3j;
    private Credentials arbiter, buyer, seller, stranger;
    private StaticGasProvider gas;
    private ShippingEscrowService service;
    private ShippingEscrow buyerContract, sellerContract, strangerContract;
    private YZHToken token, buyerToken;
    private BigInteger deadline;

    @BeforeEach
    void deployFreshContractsAndFundDisposableAccounts() throws Exception {
        transport = new HttpService(System.getenv("DHT_LOCAL_RPC"));
        web3j = Web3j.build(transport);
        assertThat(web3j.ethChainId().send().getChainId()).isEqualTo(BigInteger.valueOf(31337));
        arbiter = funded(); buyer = funded(); seller = funded(); stranger = funded();
        gas = new StaticGasProvider(BigInteger.valueOf(2_000_000_000L), BigInteger.valueOf(6_000_000));
        service = new ShippingEscrowService(web3j, arbiter, gas);
        String address = service.deploy().contractAddress();
        buyerContract = contract(address, buyer);
        sellerContract = contract(address, seller);
        strangerContract = contract(address, stranger);
        token = YZHToken.deploy(web3j, manager(arbiter), gas).send();
        token.mint(buyer.getAddress(), ONE_TOKEN.multiply(BigInteger.TEN)).send();
        buyerToken = YZHToken.load(token.getContractAddress(), web3j, manager(buyer), gas);
        buyerToken.approve(address, ONE_TOKEN.multiply(BigInteger.TEN)).send();
        deadline = headTime().add(BigInteger.valueOf(120));
    }

    @AfterEach
    void closeTransport() throws Exception {
        if (web3j != null) web3j.shutdown();
        if (transport != null) transport.close();
    }

    @Test
    void shippedOrderRejectsExpiredRefundThenCompletesWithHttpTimeline() throws Exception {
        createAndPay();
        var receipt = sellerContract.markShipped(BigInteger.ZERO).send();
        assertThat(receipt.isStatusOK()).isTrue();
        assertThat(ShippingEscrow.getOrderShippedEvents(receipt)).hasSize(1);
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Shipped");
        var mvc = MockMvcBuilders.standaloneSetup(new ShippingEscrowController(service, web3j))
                .setControllerAdvice(new EscrowApiErrorHandler()).build();
        mvc.perform(get("/api/shipping/escrow/orders/0"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Shipped"));
        expire();
        assertThatThrownBy(() -> buyerContract.refund(BigInteger.ZERO).send()).hasMessageContaining("wrong status");
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Shipped");
        assertThat(token.balanceOf(service.getContractAddress()).send()).isEqualTo(ONE_TOKEN);
        buyerContract.confirm(BigInteger.ZERO).send();
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Completed");
        assertThat(token.balanceOf(seller.getAddress()).send()).isEqualTo(ONE_TOKEN);
        mvc.perform(get("/api/shipping/escrow/orders/0/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].eventName").value("OrderShipped"))
                .andExpect(jsonPath("$[3].eventName").value("OrderCompleted"));
    }

    @Test
    void unshippedOrderRefundsOnlyAfterDeadlineAndReturnsTokens() throws Exception {
        createAndPay();
        assertThatThrownBy(() -> buyerContract.refund(BigInteger.ZERO).send()).hasMessageContaining("deadline not reached");
        expire();
        var receipt = buyerContract.refund(BigInteger.ZERO).send();
        assertThat(receipt.isStatusOK()).isTrue();
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Refunded");
        assertThat(token.balanceOf(buyer.getAddress()).send()).isEqualTo(ONE_TOKEN.multiply(BigInteger.TEN));
        assertThat(token.balanceOf(service.getContractAddress()).send()).isZero();
        assertThat(events()).containsExactly("OrderCreated", "OrderPaid", "OrderRefunded");
    }

    @Test
    void buyerStrangerAndArbiterCannotShipForSeller() throws Exception {
        createAndPay();
        for (ShippingEscrow caller : List.of(buyerContract, strangerContract, contract(service.getContractAddress(), arbiter))) {
            assertThatThrownBy(() -> caller.markShipped(BigInteger.ZERO).send()).hasMessageContaining("not the seller");
        }
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Paid");
        assertThat(sellerContract.markShipped(BigInteger.ZERO).send().isStatusOK()).isTrue();
    }

    @Test
    void createdAndAlreadyShippedOrdersCannotBeShipped() throws Exception {
        create();
        assertThatThrownBy(() -> sellerContract.markShipped(BigInteger.ZERO).send()).hasMessageContaining("wrong status");
        buyerContract.deposit(BigInteger.ZERO).send();
        sellerContract.markShipped(BigInteger.ZERO).send();
        assertThatThrownBy(() -> sellerContract.markShipped(BigInteger.ZERO).send()).hasMessageContaining("wrong status");
    }

    @Test
    void buyerCannotConfirmBeforeShippingAndOtherAccountsCannotRefund() throws Exception {
        createAndPay();
        assertThatThrownBy(() -> buyerContract.confirm(BigInteger.ZERO).send()).hasMessageContaining("wrong status");
        expire();
        assertThatThrownBy(() -> sellerContract.refund(BigInteger.ZERO).send()).hasMessageContaining("not the buyer");
        assertThatThrownBy(() -> strangerContract.refund(BigInteger.ZERO).send()).hasMessageContaining("not the buyer");
    }

    @Test
    void shippedOrderCanStillBeDisputedAndArbitrated() throws Exception {
        createAndPay();
        sellerContract.markShipped(BigInteger.ZERO).send();
        assertThatThrownBy(() -> strangerContract.dispute(BigInteger.ZERO).send()).hasMessageContaining("not the buyer or seller");
        buyerContract.dispute(BigInteger.ZERO).send();
        assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Disputed");
        assertThat(service.resolve(BigInteger.ZERO, false).orderStatus()).isEqualTo("Refunded");
        assertThat(token.balanceOf(buyer.getAddress()).send()).isEqualTo(ONE_TOKEN.multiply(BigInteger.TEN));
        assertThat(events()).containsExactly("OrderCreated", "OrderPaid", "OrderShipped", "OrderDisputed", "OrderResolved");
    }

    @Test
    void sellerCannotShipAfterDeadlineToBlockAnExpiredRefund() throws Exception {
        createAndPay();
        expire();
        assertThatThrownBy(() -> sellerContract.markShipped(BigInteger.ZERO).send()).hasMessageContaining("shipping deadline passed");
        assertThat(buyerContract.refund(BigInteger.ZERO).send().isStatusOK()).isTrue();
    }

    @Test
    void reloadChecksShippingVersionAndRejectsLegacyWithoutReplacingState() throws Exception {
        var context = service.current();
        var fresh = new ShippingEscrowService(web3j, arbiter, gas);
        assertThat(fresh.load(context.contractAddress(), new BigInteger(context.deployBlock()))).isEqualTo(context);
        var legacy = Escrow.deploy(web3j, manager(arbiter), gas, arbiter.getAddress()).send();
        assertThatThrownBy(() -> service.load(legacy.getContractAddress(), legacy.getTransactionReceipt().orElseThrow().getBlockNumber()))
                .hasMessageContaining("load failed");
        assertThat(service.current()).isEqualTo(context);
    }

    private void create() throws Exception {
        buyerContract.createOrder(seller.getAddress(), token.getContractAddress(), ONE_TOKEN, deadline, "shipping local validation").send();
    }

    private void createAndPay() throws Exception { create(); buyerContract.deposit(BigInteger.ZERO).send(); }

    private List<String> events() {
        return EscrowEventQueryService.shipping(web3j, service::current).timeline(BigInteger.ZERO)
                .stream().map(event -> event.eventName()).toList();
    }

    private BigInteger headTime() throws Exception {
        return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock().getTimestamp();
    }

    private void expire() throws Exception {
        rpc("evm_setNextBlockTimestamp", List.of(deadline.longValueExact() + 1));
        rpc("evm_mine", List.of());
        assertThat(headTime()).isGreaterThan(deadline);
    }

    private void rpc(String method, List<?> args) throws Exception {
        Response<?> response = new Request<>(method, args, transport, Response.class).send();
        assertThat(response.hasError()).as(method).isFalse();
    }

    private Credentials funded() throws Exception {
        Credentials account = Credentials.create(Keys.createEcKeyPair());
        rpc("hardhat_setBalance", List.of(account.getAddress(), "0x56bc75e2d63100000"));
        return account;
    }

    private RawTransactionManager manager(Credentials account) { return new RawTransactionManager(web3j, account, 31337L); }
    private ShippingEscrow contract(String address, Credentials account) { return ShippingEscrow.load(address, web3j, manager(account), gas); }
}
