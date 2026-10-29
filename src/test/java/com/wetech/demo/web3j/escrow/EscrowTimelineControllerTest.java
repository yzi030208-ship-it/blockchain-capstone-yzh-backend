package com.wetech.demo.web3j.escrow;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EscrowTimelineControllerTest {
    private final EscrowEventQueryService events = mock(EscrowEventQueryService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new EscrowController(mock(EscrowGateway.class), events))
            .setControllerAdvice(new EscrowApiErrorHandler()).build();

    @Test
    void exposesTimelineAsDecimalStringsAndPreservesDecodedArguments() throws Exception {
        when(events.timeline(BigInteger.ONE)).thenReturn(List.of(new TimelineEvent(
                "1", "OrderResolved", "2689270", "0", "1", "0x" + "a".repeat(64),
                Map.of("sellerWins", "false"))));
        mvc.perform(get("/api/escrow/orders/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("1"))
                .andExpect(jsonPath("$[0].blockNumber").value("2689270"))
                .andExpect(jsonPath("$[0].eventName").value("OrderResolved"))
                .andExpect(jsonPath("$[0].args.sellerWins").value("false"));
    }

    @Test
    void rejectsNegativeNonDecimalAndOverflowIdsBeforeQueryingRpc() throws Exception {
        for (String id : List.of("-1", "abc", BigInteger.ONE.shiftLeft(256).toString())) {
            mvc.perform(get("/api/escrow/orders/{id}/events", id)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(events);
    }

    @Test
    void reportsUpstreamFailureInsteadOfSuccessfulEmptyTimeline() throws Exception {
        when(events.timeline(BigInteger.ZERO)).thenThrow(new EscrowRpcException("range unavailable"));
        mvc.perform(get("/api/escrow/orders/0/events"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("range unavailable"));
    }
}
