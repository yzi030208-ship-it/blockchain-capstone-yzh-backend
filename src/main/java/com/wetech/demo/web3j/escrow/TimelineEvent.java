package com.wetech.demo.web3j.escrow;

import java.util.Map;

public record TimelineEvent(
        String orderId,
        String eventName,
        String blockNumber,
        String transactionIndex,
        String logIndex,
        String transactionHash,
        Map<String, String> args) {}
