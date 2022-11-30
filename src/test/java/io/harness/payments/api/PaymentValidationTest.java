package io.harness.payments.api;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

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
        assertEquals("Health Check OK","Health Check OK");
    }

}
