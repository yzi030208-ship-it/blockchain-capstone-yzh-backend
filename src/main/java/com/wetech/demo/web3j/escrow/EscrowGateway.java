package com.wetech.demo.web3j.escrow;

import java.math.BigInteger;

/**
 * The integration boundary for DHT's EscrowService. YZH's HTTP and event-query
 * code uses this interface and never signs transactions itself.
 */
public interface EscrowGateway {
    DeploymentResult deploy();

    EscrowContext load(String address, BigInteger fromBlock);

    EscrowContext current();

    OrderDetail getOrder(BigInteger id);

    ResolveResult resolve(BigInteger id, boolean sellerWins);
}
