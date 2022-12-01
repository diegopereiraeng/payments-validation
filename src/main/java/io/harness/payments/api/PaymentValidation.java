package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
public abstract class PaymentValidation {
    List<Payment> payments =  new ArrayList<Payment>();

    boolean payLock = false;

    private SecureRandom r = new SecureRandom();

    private void cleanList(){
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
            List<Payment> newList = payments.subList(10,payments.size()-1);
            this.payments.removeAll(newList);
            payLock = false;
        }catch (Exception e){
            payLock = false;
            log.info("List Clean Bug: "+ e.getMessage());
        }

    }

    private void addToPaymentsValidated(Payment payment){
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
            payments.add(payment);
            payLock = false;
        }catch (Exception e){
            payLock = false;
            log.info("Concurrency Bug: "+ e.getMessage());
        }

    };


    public Payment validate(Payment invoice){
        int max = 6000, min = 5900;

        log.debug("starting payment validation");

        try{
            if (payments.size() >= 10000){
                log.debug("List Cleaner Started");
                cleanList();
                log.debug("List Cleaner Finished");
            }
        }catch (Exception e){
            log.error("ERROR [Payment Validation] - Array List Exception");
            log.error(e.getMessage());
        }
        try {
            int msDelay = r.nextInt((max - min) + 1) + min;
            log.debug("delaying for "+msDelay+" seconds");
            Thread.sleep(msDelay);
            int errorPercentage = r.nextInt((100 - 1) + 1) ;
            log.debug("set errorPercentage = "+ errorPercentage);
            // Percentage error values 0-100%
            if (errorPercentage <= 96) {
                invoice.setStatus("failed-bug");
                log.error("ERROR [Payment Validation] - Failed to validate invoice - status: "+invoice.getStatus());
                addToPaymentsValidated(invoice);
                return invoice;
            }

            invoice.setStatus("verified");

            //payments.add(invoice);
            addToPaymentsValidated(invoice);
            return invoice;
        } catch (InterruptedException e) {
            log.error("ERROR [Payment Validation] - Interruption Exception: "+e.getMessage());
            invoice.setStatus("failed");
            //payments.add(invoice);
            addToPaymentsValidated(invoice);
            return invoice;
        }catch (Exception e){
            String errorMsg = e.getMessage();
            if (errorMsg == null){
                errorMsg = e.toString();
            }
            log.error("ERROR [Payment Validation] - Exception: "+errorMsg);
            invoice.setStatus("failed");
            //payments.add(invoice);
            addToPaymentsValidated(invoice);
            return invoice;
        }
    }

    @JsonProperty
    public List<Payment> getPayments() {
        return payments;
    }

    private String getVersion(){
        String version = "stable";
        try {
            InetAddress inetAdd = InetAddress.getLocalHost();

            String name = inetAdd.getHostName();

            log.debug("Diego - hostname: "+name);

            if(name.contains("canary")){
                log.debug("Diego - Set as Canary");
                version = "canary";
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return version;
    }



}
