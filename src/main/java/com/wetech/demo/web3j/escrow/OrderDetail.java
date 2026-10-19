package com.wetech.demo.web3j.escrow;

public record OrderDetail(
        String id,
        String buyer,
        String seller,
        String token,
        String amount,
        String deadline,
        String status,
        String description) {}
