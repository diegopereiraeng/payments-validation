package io.harness.payments.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTest {
    private static final ObjectMapper MAPPER = newObjectMapper();

//    @Test
//    void seralizesToJSON() throws Exception {
//        final Payment payment = new Payment(23216);
//        payment.setVersion("stable");
//        payment.setErrorMsg("Testing Error Msg");
//        payment.setStatus("failed-bug");
//
//        final String expected = MAPPER.writeValueAsString(
//                MAPPER.readValue(getClass().getResource("/fixtures/Payment.json"), Payment.class));
//
//        assertThat(MAPPER.writeValueAsString(payment)).isEqualTo(expected);
//    }

//    @Test
//    public void deserializesFromJSON() throws Exception {
//        final Payment payment = new Payment(23216);
//        payment.setVersion("stable");
//        payment.setErrorMsg("Testing Error Msg");
//        payment.setStatus("failed-bug");
//        assertThat(MAPPER.readValue(getClass().getResource("/fixtures/Payment.json"), Payment.class))
//                .isEqualTo(payment);
//    }
}
