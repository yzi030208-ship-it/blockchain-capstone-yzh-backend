package com.wetech.demo.web3j.escrow;

public record ResolveResult(String transactionHash, String blockNumber, String receiptStatus, String orderStatus) {}
