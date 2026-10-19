package com.wetech.demo.web3j.escrow;

import java.math.BigInteger;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/escrow")
public class EscrowController {
    private final EscrowGateway gateway;
    private final EscrowEventQueryService events;

    public EscrowController(EscrowGateway gateway, EscrowEventQueryService events) {
        this.gateway = gateway;
        this.events = events;
    }

    @PostMapping("/deploy")
    public DeploymentResult deploy() {
        return gateway.deploy();
    }

    @PostMapping("/load")
    public EscrowContext load(@RequestParam String address, @RequestParam String fromBlock) {
        if (!address.matches("(?i)^0x[0-9a-f]{40}$")) {
            throw new IllegalArgumentException("address must be a 20-byte hex address");
        }
        return gateway.load(address, nonnegative(fromBlock, "fromBlock"));
    }

    @GetMapping("/address")
    public EscrowContext address() {
        return gateway.current();
    }

    @GetMapping("/orders/{id}")
    public OrderDetail order(@PathVariable String id) {
        return gateway.getOrder(nonnegative(id, "id"));
    }

    @GetMapping("/orders")
    public List<OrderCreatedView> orders() {
        return events.listOrders();
    }

    @PostMapping("/orders/{id}/resolve")
    public ResolveResult resolve(@PathVariable String id, @RequestParam String sellerWins) {
        if (!sellerWins.equals("true") && !sellerWins.equals("false")) {
            throw new IllegalArgumentException("sellerWins must be true or false");
        }
        return gateway.resolve(nonnegative(id, "id"), Boolean.parseBoolean(sellerWins));
    }

    @GetMapping("/orders/{id}/events")
    public List<TimelineEvent> orderEvents(@PathVariable String id) {
        return events.timeline(nonnegative(id, "id"));
    }

    private static BigInteger nonnegative(String raw, String name) {
        if (raw == null || !raw.matches("[0-9]+")) {
            throw new IllegalArgumentException(name + " must be a nonnegative decimal integer");
        }
        BigInteger value = new BigInteger(raw);
        if (value.bitLength() > 256) {
            throw new IllegalArgumentException(name + " exceeds uint256");
        }
        return value;
    }
}
