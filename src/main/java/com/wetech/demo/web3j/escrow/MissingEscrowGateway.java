package com.wetech.demo.web3j.escrow;

import java.math.BigInteger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Lets the SimpleStorage example run before DHT supplies EscrowService. */
public class MissingEscrowGateway implements EscrowGateway {
    private ResponseStatusException pending() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "EscrowService is not integrated yet; DHT must implement EscrowGateway");
    }

    @Override public DeploymentResult deploy() { throw pending(); }
    @Override public EscrowContext load(String address, BigInteger fromBlock) { throw pending(); }
    @Override public EscrowContext current() { throw pending(); }
    @Override public OrderDetail getOrder(BigInteger id) { throw pending(); }
    @Override public ResolveResult resolve(BigInteger id, boolean sellerWins) { throw pending(); }
}
