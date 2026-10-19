package com.wetech.demo.web3j.escrow;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

/** Rebuilds the order list and timelines from the actual Escrow event logs. */
@Service
public class EscrowEventQueryService {
    private static final Event CREATED = new Event("OrderCreated", List.of(
            new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {},
            new TypeReference<Address>(true) {}, new TypeReference<Address>() {},
            new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    private static final Event PAID = singleId("OrderPaid");
    private static final Event COMPLETED = singleId("OrderCompleted");
    private static final Event REFUNDED = singleId("OrderRefunded");
    private static final Event DISPUTED = new Event("OrderDisputed", List.of(
            new TypeReference<Uint256>(true) {}, new TypeReference<Address>() {}));
    private static final Event RESOLVED = new Event("OrderResolved", List.of(
            new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}));
    private static final Map<String, Event> EVENTS = Map.of(
            EventEncoder.encode(CREATED), CREATED,
            EventEncoder.encode(PAID), PAID,
            EventEncoder.encode(COMPLETED), COMPLETED,
            EventEncoder.encode(REFUNDED), REFUNDED,
            EventEncoder.encode(DISPUTED), DISPUTED,
            EventEncoder.encode(RESOLVED), RESOLVED);
    private static final Comparator<Log> LOG_ORDER = Comparator
            .comparing(Log::getBlockNumber)
            .thenComparing(Log::getTransactionIndex)
            .thenComparing(Log::getLogIndex);

    private final Web3j web3j;
    private final EscrowGateway gateway;

    public EscrowEventQueryService(Web3j web3j, EscrowGateway gateway) {
        this.web3j = web3j;
        this.gateway = gateway;
    }

    public List<OrderCreatedView> listOrders() {
        List<OrderCreatedView> orders = new ArrayList<>();
        for (Log log : scan(List.of(EventEncoder.encode(CREATED)), null)) {
            List<Type> data = decode(log, CREATED);
            if (data.size() != 3 || log.getTopics().size() != 4) {
                throw new EscrowRpcException("Unexpected OrderCreated log layout at " + log.getTransactionHash());
            }
            orders.add(new OrderCreatedView(
                    indexedId(log).toString(), indexedAddress(log.getTopics().get(2)),
                    indexedAddress(log.getTopics().get(3)),
                    ((Address) data.get(0)).getValue(),
                    ((Uint256) data.get(1)).getValue().toString(),
                    ((Uint256) data.get(2)).getValue().toString(),
                    log.getBlockNumber().toString(), log.getTransactionHash()));
        }
        return orders;
    }

    public List<TimelineEvent> timeline(BigInteger id) {
        if (id.signum() < 0) throw new IllegalArgumentException("id must be nonnegative");
        List<TimelineEvent> timeline = new ArrayList<>();
        for (Log log : scan(List.copyOf(EVENTS.keySet()), id)) {
            Event event = EVENTS.get(log.getTopics().get(0).toLowerCase(Locale.ROOT));
            if (event == null) continue;
            List<Type> data = decode(log, event);
            Map<String, String> args = new LinkedHashMap<>();
            if (event == CREATED) {
                if (log.getTopics().size() != 4 || data.size() != 3) {
                    throw new EscrowRpcException("Unexpected OrderCreated log layout at " + log.getTransactionHash());
                }
                args.put("buyer", indexedAddress(log.getTopics().get(2)));
                args.put("seller", indexedAddress(log.getTopics().get(3)));
                args.put("token", ((Address) data.get(0)).getValue());
                args.put("amount", ((Uint256) data.get(1)).getValue().toString());
                args.put("deadline", ((Uint256) data.get(2)).getValue().toString());
            } else if (event == DISPUTED) {
                args.put("by", ((Address) data.get(0)).getValue());
            } else if (event == RESOLVED) {
                args.put("sellerWins", ((Bool) data.get(0)).getValue().toString());
            }
            timeline.add(new TimelineEvent(
                    id.toString(), event.getName(),
                    log.getBlockNumber().toString(), log.getTransactionIndex().toString(),
                    log.getLogIndex().toString(), log.getTransactionHash(), args));
        }
        return timeline;
    }

    private List<Log> scan(List<String> topic0, BigInteger id) {
        EscrowContext context = gateway.current();
        String address = context.contractAddress();
        if (address == null || !address.matches("(?i)^0x[0-9a-f]{40}$")) {
            throw new IllegalStateException("No valid Escrow contract is loaded");
        }
        BigInteger fromBlock;
        try {
            fromBlock = new BigInteger(context.deployBlock());
        } catch (RuntimeException error) {
            throw new IllegalStateException("Escrow deployment block is missing or invalid", error);
        }
        if (fromBlock.signum() < 0) throw new IllegalStateException("Escrow deployment block is negative");

        BigInteger latest;
        try {
            EthBlockNumber response = web3j.ethBlockNumber().send();
            if (response.hasError()) throw new EscrowRpcException("eth_blockNumber failed: " + response.getError().getMessage());
            latest = response.getBlockNumber();
        } catch (IOException error) {
            throw new EscrowRpcException("eth_blockNumber request failed", error);
        }

        List<Log> raw = new ArrayList<>();
        BigInteger start = fromBlock;
        while (start.compareTo(latest) <= 0) {
            BigInteger end = start.add(BigInteger.valueOf(9)).min(latest);
            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(start), DefaultBlockParameter.valueOf(end), address);
            filter.addOptionalTopics(topic0.toArray(String[]::new));
            if (id != null) filter.addSingleTopic("0x" + String.format("%064x", id));
            try {
                EthLog response = web3j.ethGetLogs(filter).send();
                if (response.hasError()) {
                    throw new EscrowRpcException("eth_getLogs failed for blocks " + start + "-" + end
                            + ": " + response.getError().getMessage());
                }
                if (response.getLogs() == null) {
                    throw new EscrowRpcException("eth_getLogs returned no result for blocks " + start + "-" + end);
                }
                for (EthLog.LogResult<?> result : response.getLogs()) {
                    if (!(result.get() instanceof Log log)) {
                        throw new EscrowRpcException("eth_getLogs returned an unexpected log type");
                    }
                    if (!log.isRemoved()) raw.add(log);
                }
            } catch (IOException error) {
                throw new EscrowRpcException("eth_getLogs request failed for blocks " + start + "-" + end, error);
            }
            start = end.add(BigInteger.ONE);
        }
        raw.sort(LOG_ORDER);
        Map<String, Log> distinct = new LinkedHashMap<>();
        for (Log log : raw) {
            if (log.getTopics().isEmpty()) continue;
            String firstTopic = log.getTopics().get(0).toLowerCase(Locale.ROOT);
            if (!topic0.contains(firstTopic)) continue;
            if (id != null && !id.equals(indexedId(log))) continue;
            distinct.putIfAbsent(log.getTransactionHash().toLowerCase(Locale.ROOT) + ":" + log.getLogIndex(), log);
        }
        return List.copyOf(distinct.values());
    }

    private static List<Type> decode(Log log, Event event) {
        return FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
    }

    private static BigInteger indexedId(Log log) {
        if (log.getTopics().size() < 2) throw new EscrowRpcException("Event missing indexed order id");
        return new BigInteger(log.getTopics().get(1).substring(2), 16);
    }

    private static String indexedAddress(String topic) {
        if (topic == null || topic.length() != 66) throw new EscrowRpcException("Malformed indexed address");
        return "0x" + topic.substring(26).toLowerCase(Locale.ROOT);
    }

    private static Event singleId(String name) {
        return new Event(name, List.of(new TypeReference<Uint256>(true) {}));
    }
}
