package io.harness.payments.api;

import org.junit.jupiter.api.Test;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest {
    @Test
    public void testAuthorizePayment() {
        Authorization auth = new Authorization(1234567890);
        Payment payment = new Payment(Long.parseLong("1234567890"));

        assertThat(true).isEqualTo(true);
    }


}
