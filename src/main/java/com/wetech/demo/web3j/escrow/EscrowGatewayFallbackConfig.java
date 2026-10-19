package com.wetech.demo.web3j.escrow;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EscrowGatewayFallbackConfig {
    @Bean
    @ConditionalOnMissingBean(EscrowGateway.class)
    public EscrowGateway missingEscrowGateway() {
        return new MissingEscrowGateway();
    }
}
