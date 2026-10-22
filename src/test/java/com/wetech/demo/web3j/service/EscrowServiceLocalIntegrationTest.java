package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.escrow.Escrow;
import com.wetech.demo.web3j.contracts.yzhtoken.YZHToken;
import com.wetech.demo.web3j.escrow.*;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import static org.assertj.core.api.Assertions.*;

/** Real EVM execution on an explicitly enabled disposable local Hardhat node. */
@EnabledIfEnvironmentVariable(named = "DHT_LOCAL_RPC", matches = "http://(127\\.0\\.0\\.1|localhost):[0-9]+")
class EscrowServiceLocalIntegrationTest {
    @Test
    void deploymentReloadOrdersArbitrationAndEventsRunOnLocalEvm() throws Exception {
        HttpService transport = new HttpService(System.getenv("DHT_LOCAL_RPC"));
        try {
            Web3j web3j = Web3j.build(transport);
            try {
                assertThat(web3j.ethChainId().send().getChainId()).isEqualTo(BigInteger.valueOf(31337));
                Credentials backend = funded(transport);
                Credentials buyer = funded(transport);
                Credentials seller = funded(transport);
                var gas = new StaticGasProvider(BigInteger.valueOf(2_000_000_000L), BigInteger.valueOf(6_000_000));
                var manager = new RawTransactionManager(web3j, backend, 31337L);
                var service = new EscrowService(web3j, backend, gas);
                DeploymentResult deployment = service.deploy();
                assertThat(deployment.transactionHash()).matches("0x[0-9a-fA-F]{64}");
                assertThat(service.current().arbiter()).isEqualToIgnoringCase(backend.getAddress());
                var reloaded = new EscrowService(web3j, backend, gas);
                assertThat(reloaded.load(deployment.contractAddress(), new BigInteger(deployment.deployBlock())))
                        .isEqualTo(service.current());
                assertThat(reloaded.getDeployBlock().toString()).isEqualTo(deployment.deployBlock());

                YZHToken token = YZHToken.deploy(web3j, manager, gas).send();
                BigInteger amount = BigInteger.TEN.pow(18).multiply(BigInteger.valueOf(30));
                token.mint(buyer.getAddress(), amount.multiply(BigInteger.valueOf(3))).send();
                Escrow buyersEscrow = Escrow.load(deployment.contractAddress(), web3j,
                        new RawTransactionManager(web3j, buyer, 31337L), gas);
                Escrow sellersEscrow = Escrow.load(deployment.contractAddress(), web3j,
                        new RawTransactionManager(web3j, seller, 31337L), gas);
                BigInteger deadline = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                        .send().getBlock().getTimestamp().add(BigInteger.valueOf(1800));
                for (int i = 0; i < 3; i++) {
                    buyersEscrow.createOrder(seller.getAddress(), token.getContractAddress(), amount,
                            deadline, "local test order " + i).send();
                    assertThat(service.getOrder(BigInteger.valueOf(i)).status()).isEqualTo("Created");
                }
                YZHToken buyersToken = YZHToken.load(token.getContractAddress(), web3j,
                        new RawTransactionManager(web3j, buyer, 31337L), gas);
                buyersToken.approve(deployment.contractAddress(), amount.multiply(BigInteger.valueOf(3))).send();
                for (int i = 0; i < 3; i++) buyersEscrow.deposit(BigInteger.valueOf(i)).send();
                assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Paid");
                assertThatThrownBy(() -> service.getOrder(BigInteger.valueOf(99)))
                        .isInstanceOf(EscrowRpcException.class);
                assertThatThrownBy(() -> service.resolve(BigInteger.ZERO, true))
                        .isInstanceOf(EscrowRpcException.class).hasMessageContaining("resolve failed");
                assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Paid");
                sellersEscrow.dispute(BigInteger.ONE).send();
                assertThat(service.getOrder(BigInteger.ONE).status()).isEqualTo("Disputed");
                ResolveResult refund = service.resolve(BigInteger.ONE, false);
                assertThat(refund.receiptStatus()).isEqualTo("0x1");
                assertThat(refund.orderStatus()).isEqualTo("Refunded");
                assertThat(token.balanceOf(buyer.getAddress()).send()).isEqualTo(amount);
                buyersEscrow.confirm(BigInteger.ZERO).send();
                assertThat(service.getOrder(BigInteger.ZERO).status()).isEqualTo("Completed");
                sellersEscrow.dispute(BigInteger.TWO).send();
                assertThat(service.resolve(BigInteger.TWO, true).orderStatus()).isEqualTo("Completed");
                assertThat(token.balanceOf(seller.getAddress()).send()).isEqualTo(amount.multiply(BigInteger.TWO));

                // Exercise YZH's existing event code against real generated contract logs.
                var events = new EscrowEventQueryService(web3j, reloaded);
                assertThat(events.listOrders()).hasSize(3);
                assertThat(events.timeline(BigInteger.ONE)).hasSize(4);
                var previous = reloaded.current();
                assertThatThrownBy(() -> reloaded.load(buyer.getAddress(), BigInteger.ZERO))
                        .isInstanceOf(IllegalArgumentException.class);
                assertThat(reloaded.current()).isEqualTo(previous);
                var wrongBackend = new EscrowService(web3j, buyer, gas);
                assertThatThrownBy(() -> wrongBackend.load(deployment.contractAddress(),
                        new BigInteger(deployment.deployBlock())))
                        .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("arbiter");
            } finally {
                web3j.shutdown();
            }
        } finally {
            transport.close();
        }
    }

    private static Credentials funded(HttpService transport) throws Exception {
        // Random disposable keys stay in memory and are never printed or committed.
        Credentials credentials = Credentials.create(Keys.createEcKeyPair());
        Response<?> response = new Request<>("hardhat_setBalance",
                List.of(credentials.getAddress(), "0x56bc75e2d63100000"), transport, Response.class).send();
        assertThat(response.hasError()).isFalse();
        return credentials;
    }
}
