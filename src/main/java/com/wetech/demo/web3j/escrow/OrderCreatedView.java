package com.wetech.demo.web3j.escrow;

public record OrderCreatedView(
        String id,
        String buyer,
        String seller,
        String token,
        String amount,
        String deadline,
        String blockNumber,
        String transactionHash) {}
