package com.wetech.demo.web3j.escrow;

public class EscrowRpcException extends RuntimeException {
    public EscrowRpcException(String message) { super(message); }
    public EscrowRpcException(String message, Throwable cause) { super(message, cause); }
}
