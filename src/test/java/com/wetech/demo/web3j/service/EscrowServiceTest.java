package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.escrow.*;
import com.wetech.demo.web3j.Application;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EscrowServiceTest {
    @Test
    void missingStateAndInvalidArgumentsNeverSendTransactions() throws Exception {
        EscrowService service = new EscrowService(mock(Web3j.class),
                Credentials.create(Keys.createEcKeyPair()),
                new StaticGasProvider(BigInteger.ONE, BigInteger.valueOf(6_000_000)));
        assertThatThrownBy(service::current).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.getOrder(BigInteger.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.resolve(BigInteger.ONE.shiftLeft(256), false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.load("not-an-address", BigInteger.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void springSelectsRealGatewayInsteadOfBaselineFallback() throws Exception {
        Credentials credentials = Credentials.create(Keys.createEcKeyPair());
        new ApplicationContextRunner()
                .withPropertyValues("web3j.client-address=http://127.0.0.1:18545",
                        "web3j.private-key=" + credentials.getEcKeyPair().getPrivateKey().toString(16),
                        "web3j.gas-price=2000000000", "web3j.gas-limit=6000000")
                .withUserConfiguration(Application.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(EscrowGateway.class);
                    assertThat(context.getBean(EscrowGateway.class)).isInstanceOf(EscrowService.class);
                });
    }
}
