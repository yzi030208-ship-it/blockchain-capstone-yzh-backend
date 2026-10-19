package com.wetech.demo.web3j.escrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

class EscrowEventQueryServiceTest {
    private static final String ESCROW = "0x1111111111111111111111111111111111111111";
    private static final String BUYER = "0x2222222222222222222222222222222222222222";
    private static final String SELLER = "0x3333333333333333333333333333333333333333";
    private static final String TOKEN = "0x4444444444444444444444444444444444444444";
    private static final String CREATED_TOPIC = EventEncoder.encode(new Event("OrderCreated", List.of(
            new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {},
            new TypeReference<Address>(true) {}, new TypeReference<Address>() {},
            new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {})));
    private static final String PAID_TOPIC = EventEncoder.encode(new Event("OrderPaid", List.of(
            new TypeReference<Uint256>(true) {})));

    @Test
    void scansTenBlockWindowsAndDeduplicatesTimelineLogs() throws Exception {
        ObjectMapper json = new ObjectMapper();
        List<String> ranges = new ArrayList<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            JsonNode request = json.readTree(exchange.getRequestBody());
            String method = request.get("method").asText();
            String result;
            if (method.equals("eth_blockNumber")) {
                result = "\"0x70\""; // block 112
            } else {
                JsonNode filter = request.get("params").get(0);
                String from = filter.get("fromBlock").asText();
                String to = filter.get("toBlock").asText();
                ranges.add(from + "-" + to);
                result = from.equals("0x64")
                        ? "[" + createdLog() + "]"
                        : "[" + paidLog() + "," + paidLog() + "]";
            }
            byte[] body = ("{\"jsonrpc\":\"2.0\",\"id\":" + request.get("id")
                    + ",\"result\":" + result + "}").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) { out.write(body); }
        });
        server.start();
        try (Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:" + server.getAddress().getPort()))) {
            EscrowGateway gateway = new MissingEscrowGateway() {
                @Override public EscrowContext current() { return new EscrowContext(ESCROW, BUYER, "100"); }
            };
            EscrowEventQueryService service = new EscrowEventQueryService(web3j, gateway);
            List<OrderCreatedView> orders = service.listOrders();
            assertEquals(1, orders.size());
            assertEquals("0", orders.get(0).id());
            assertEquals("30", orders.get(0).amount());
            List<TimelineEvent> timeline = service.timeline(BigInteger.ZERO);
            assertEquals(List.of("OrderCreated", "OrderPaid"), timeline.stream().map(TimelineEvent::eventName).toList());
            assertEquals(List.of("0x64-0x6d", "0x6e-0x70", "0x64-0x6d", "0x6e-0x70"), ranges);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void reportsRpcErrorsInsteadOfReturningAnEmptyOrderList() throws Exception {
        ObjectMapper json = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            JsonNode request = json.readTree(exchange.getRequestBody());
            String payload = request.get("method").asText().equals("eth_blockNumber")
                    ? "\"0x64\""
                    : null;
            String bodyText = "{\"jsonrpc\":\"2.0\",\"id\":" + request.get("id")
                    + (payload == null
                        ? ",\"error\":{\"code\":-32000,\"message\":\"range unavailable\"}}"
                        : ",\"result\":" + payload + "}");
            byte[] body = bodyText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) { out.write(body); }
        });
        server.start();
        try (Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:" + server.getAddress().getPort()))) {
            EscrowGateway gateway = new MissingEscrowGateway() {
                @Override public EscrowContext current() { return new EscrowContext(ESCROW, BUYER, "100"); }
            };
            EscrowRpcException error = assertThrows(EscrowRpcException.class,
                    () -> new EscrowEventQueryService(web3j, gateway).listOrders());
            assertTrue(error.getMessage().contains("range unavailable"));
        } finally {
            server.stop(0);
        }
    }

    private static String createdLog() {
        String data = FunctionEncoder.encodeConstructor(List.of(
                new Address(TOKEN), new Uint256(BigInteger.valueOf(30)), new Uint256(BigInteger.valueOf(999))));
        return log(100, 0, 0, "0x" + "a".repeat(64),
                "[\"" + CREATED_TOPIC + "\",\"" + word(0) + "\",\"" + addressWord(BUYER)
                        + "\",\"" + addressWord(SELLER) + "\"]", data);
    }

    private static String paidLog() {
        return log(111, 0, 1, "0x" + "b".repeat(64),
                "[\"" + PAID_TOPIC + "\",\"" + word(0) + "\"]", "0x");
    }

    private static String log(int block, int txIndex, int logIndex, String hash, String topics, String data) {
        return "{\"address\":\"" + ESCROW + "\",\"blockNumber\":\"0x" + Integer.toHexString(block)
                + "\",\"transactionIndex\":\"0x" + Integer.toHexString(txIndex)
                + "\",\"logIndex\":\"0x" + Integer.toHexString(logIndex)
                + "\",\"transactionHash\":\"" + hash + "\",\"blockHash\":\"0x" + "c".repeat(64)
                + "\",\"data\":\"" + data + "\",\"topics\":" + topics + ",\"removed\":false}";
    }

    private static String word(int value) { return "0x" + String.format("%064x", value); }
    private static String addressWord(String value) { return "0x" + "0".repeat(24) + value.substring(2); }
}
