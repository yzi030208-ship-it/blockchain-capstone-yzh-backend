package com.wetech.demo.web3j.config;

import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

@Configuration
public class Web3jConfig {
    @Bean
    public Web3j web3j(@Value("${web3j.client-address}") String rpcUrl) {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public Credentials credentials(@Value("${web3j.private-key:}") String privateKey) {
        if (privateKey == null || privateKey.isBlank()) {
            throw new IllegalStateException("Set WEB3J_PRIVATE_KEY in the local environment before starting the backend");
        }
        return Credentials.create(privateKey.strip());
    }

    @Bean
    public ContractGasProvider contractGasProvider(
            @Value("${web3j.gas-price}") BigInteger gasPrice,
            @Value("${web3j.gas-limit}") BigInteger gasLimit) {
        return new StaticGasProvider(gasPrice, gasLimit);
    }
}
