package io.harness.payments.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.jupiter.api.Test;


import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static io.dropwizard.jackson.Jackson.newObjectMapper;



import static org.junit.Assert.*;
@Slf4j
public class PaymentValidationTest {

    List<Payment> payments =  new ArrayList<Payment>();

    boolean payLock = false;

    private SecureRandom r = new SecureRandom();

    Client client = ClientBuilder.newClient();

    @Before
    public void setUp() throws Exception {
        System.out.println("Initializing Requirements for Unit Test");

        assertEquals("Health Check OK","Health Check OK");

    }



    @Test
    public void validate() {
        System.out.println("Unit Test validate");

        Payment invoice = new Payment(2);

        final ObjectMapper MAPPER = newObjectMapper();

        final URL url;
        final String response;
        try {

            try {
                int max = 1000, min = 900;



                log.info("starting payment validation");

                try {
                    int msDelay = r.nextInt((max - min) + 1) + min;
                    log.debug("delaying for "+msDelay+" seconds");
                    Thread.sleep(msDelay);

                    invoice.setStatus("verified");


                } catch (InterruptedException e) {
                    log.error("ERROR [Payment Validation] - Interruption Exception: "+e.getMessage());
                    invoice.setStatus("failed");
                    //payments.add(invoice);


                }catch (Exception e){
                    String errorMsg = e.getMessage();
                    if (errorMsg == null){
                        errorMsg = e.toString();
                    }
                    log.error("ERROR [Payment Validation] - Exception: "+errorMsg);
                    invoice.setStatus("failed");
                    //payments.add(invoice);

                }
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
            }

            log.error("Error to validate");


        }catch (Exception e){
            invoice = new Payment(3);
        }

        assertEquals("verified", invoice.getStatus());

    }

    @Test
    public void getPayments() {
        System.out.println("Unit Test getPayments");
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) {

        }

        log.error("Error to list payments validations");

        assertEquals("Health Check OK","Health Check OK");
    }
    @Test
    public void addToPaymentsValidatedTest() {
        System.out.println("Unit Test addToPaymentsValidated");
        System.out.println("");
        log.info("List Size before clean: "+this.payments.size());
        System.out.println("");
        long beforeAdd = this.payments.size();

        addToPaymentsValidated(new Payment(1));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(beforeAdd+1, this.payments.size());


        try{
            CustomerMethod();
        }catch (InternalServerErrorException e){
            log.error("Error to add payments to validated list");
        }

    }


    public void CustomerMethod(){
        throw new InternalServerErrorException("Harness Error Tracking see everything");
    }
    public void addToPaymentsValidated(Payment invoice) {


        int listSizeBefore = this.payments.size();
        try {

            while (payLock)
            {
                log.debug("Waiting for Thread Unlock");
                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    log.error("Thread Safe Error: "+e.getMessage());
                    payLock = false;
                    break;
                }
            }
            try{
                payLock = true;
                payments.add(invoice);
                payLock = false;
            }catch (Exception e){
                payLock = false;
                log.info("Concurrency Bug: "+ e.getMessage());
            }
        }catch (Exception e){
            throw e;
        }




    }

    @Test
    public void cleanList() {
        System.out.println("Unit Test cleanList");

        System.out.println("Adding payments to list...");

        for (int i = 0; i < 1313; i++) {
            Payment invoice = new Payment(i);
            addToPaymentsValidated(invoice);
        }

        log.info("List Size before clean: "+this.payments.size());

        try {
            Thread.sleep(500);
            while (payLock)
            {
                log.debug("Waiting for Thread Unlock");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("Thread Safe Error: "+e.getMessage());
                    break;
                }
            }
            try{
                payLock = true;
                List<Payment> newList = payments.subList(0,payments.size());
                this.payments.removeAll(newList);
                payLock = false;
            }catch (Exception e){
                payLock = false;
                log.info("List Clean Bug: "+ e.getMessage());
            }
        }
        catch (InterruptedException ex) {

        }

        log.info("List Size after clean: "+this.payments.size());

        log.error("Error to clean validation list");

        log.error("Error Tracking track all your errors");

        assertEquals(0,payments.size());

        // Uncomment to make build test fail and show exception in error tracking
        //throw new RuntimeException("Error Tracking is Awesome");
    }

}
