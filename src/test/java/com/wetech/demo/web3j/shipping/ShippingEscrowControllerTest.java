package com.wetech.demo.web3j.shipping;

import com.wetech.demo.web3j.escrow.*;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ShippingEscrowControllerTest {
    @Test
    void returnsShippedTextAndValidatesIdBeforeAnyChainCall() throws Exception {
        var service = mock(ShippingEscrowService.class);
        when(service.getOrder(BigInteger.ZERO)).thenReturn(new OrderDetail("0", "buyer", "seller", "token", "1", "999", "Shipped", "test"));
        var mvc = MockMvcBuilders.standaloneSetup(new ShippingEscrowController(service, mock(Web3j.class)))
                .setControllerAdvice(new EscrowApiErrorHandler()).build();
        mvc.perform(get("/api/shipping/escrow/orders/0")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Shipped"));
        clearInvocations(service);
        mvc.perform(get("/api/shipping/escrow/orders/-1")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingContextIsReportedInsteadOfFabricatingAnOrder() throws Exception {
        var service = mock(ShippingEscrowService.class);
        when(service.getOrder(BigInteger.ZERO)).thenThrow(new IllegalStateException("Shipping contract not loaded"));
        var mvc = MockMvcBuilders.standaloneSetup(new ShippingEscrowController(service, mock(Web3j.class)))
                .setControllerAdvice(new EscrowApiErrorHandler()).build();
        mvc.perform(get("/api/shipping/escrow/orders/0")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Shipping contract not loaded"));
    }
}
