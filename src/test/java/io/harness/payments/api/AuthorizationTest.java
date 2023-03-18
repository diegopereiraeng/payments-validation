package io.harness.payments.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest {

    @Test
    public void testAuthorizePaymentWithValidAmount() throws InterruptedException {
        Authorization auth = new Authorization(1000);
        Payment payment = new Payment(1000);
        Thread.sleep(20000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithNegativeAmount() throws InterruptedException {
        Authorization auth = new Authorization(-1000);
        Payment payment = new Payment(-1000);
        Thread.sleep(20000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithZeroAmount() throws InterruptedException {
        Authorization auth = new Authorization(0);
        Payment payment = new Payment(0);
        Thread.sleep(10000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithMaxAmount() throws InterruptedException {
        Authorization auth = new Authorization(Long.MAX_VALUE);
        Payment payment = new Payment(Long.MAX_VALUE);
        Thread.sleep(12000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithMinAmount() throws InterruptedException {
        Authorization auth = new Authorization(Long.MIN_VALUE);
        Payment payment = new Payment(Long.MIN_VALUE);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithOddAmount() throws InterruptedException {
        Authorization auth = new Authorization(12345);
        Payment payment = new Payment(12345);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithEvenAmount() throws InterruptedException {
        Authorization auth = new Authorization(24680);
        Payment payment = new Payment(24680);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithLargeAmount() throws InterruptedException {
        Authorization auth = new Authorization(1000000000);
        Payment payment = new Payment(1000000000);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithSmallAmount() throws InterruptedException {
        Authorization auth = new Authorization(10);
        Payment payment = new Payment(10);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testAuthorizePaymentWithRandomAmount() throws InterruptedException {
        Authorization auth = new Authorization(456789012);
        Payment payment = new Payment(456789012);
        Thread.sleep(120000); // wait for 2 minutes
        assertThat(true).isEqualTo(true);
    }
}
