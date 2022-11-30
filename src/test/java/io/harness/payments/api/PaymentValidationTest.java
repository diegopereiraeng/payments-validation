package io.harness.payments.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
@Slf4j
public class PaymentValidationTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("Initializing Requirements for Unit Test");

        assertEquals("Health Check OK","Health Check OK");

    }

    @Test
    public void validate() {
        System.out.println("Unit Test validate");
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException ex) {

        }

        log.error("Error to validate");

        assertEquals("Health Check OK","Health Check OK");
    }

    @Test
    public void getPayments() {
        System.out.println("Unit Test getPayments");
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException ex) {

        }

        log.error("Error to list payments validations");

        assertEquals("Health Check OK","Health Check OK");
    }
    @Test
    public void addToPaymentsValidated() {
        System.out.println("Unit Test addToPaymentsValidated");
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {

        }

        log.error("Error to add payments to validated list");

        assertEquals("Health Check OK","Health Check OK");
    }

    @Test
    public void cleanList() {
        System.out.println("Unit Test cleanList");
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException ex) {

        }

        log.error("Error to clean validation list");

        log.error("Error Tracking track all your errors");

        assertEquals("Health Check OK","Health Check OK");

        // Uncomment to make build test fail and show exception in error tracking
        //throw new RuntimeException("Error Tracking is Awesome");
    }

}
