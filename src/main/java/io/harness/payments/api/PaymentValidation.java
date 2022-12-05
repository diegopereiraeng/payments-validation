package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class PaymentValidation {
    List<Payment> payments =  new ArrayList<Payment>();

    private Map<Double, Long> authorizations
            = new ConcurrentHashMap<Double, Long>();

    private boolean payLock = false;

    private boolean authorizationLock = false;

    private boolean betaFeature = false;

    public void setBetaFeature(){
        this.betaFeature = true;
    }

    public void disableBetaFeature(){
        this.betaFeature = false;
    }

    private boolean enableAuthorization = true;

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

    public double getAuthorization(long invoiceID){

        double authorizationID = r.nextDouble() * (900000 - 100000) + 100000 ;
        this.authorizations.put(authorizationID,invoiceID);
        return authorizationID;
    }

    public boolean authorize(Payment invoice){
        while (authorizationLock)
        {
            log.debug("Waiting for Thread Unlock");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("Thread Safe Error: "+e.getMessage());
                break;
            }
        }
        authorizationLock = true;
//        for ( String key : authorizations.keySet() ) {
//            System.out.println( key );
//            if(Objects.equals(key, invoice.getValidationID() )){
//                if (Objects.equals(authorizations.get(key), invoice.getId() )){
//                    authorizationLock = false;
//                    authorizations.remove(key);
//                    return true; //return the first found
//                }
//                log.error("Invalid Invoice ID");
//            }
//
//        }
        log.info("Diego");
        log.info(authorizations.keySet().toString());
        if (Objects.equals(authorizations.get(invoice.getValidationID()), invoice.getId() )){
            authorizationLock = false;
            authorizations.remove(invoice.getValidationID());
            return true; //return the first found
        }
        log.error("Invalid Invoice ID");

        authorizationLock = false;
        return false;
    }

    public Payment validate(Payment invoice){

        // Set here the Mex and Min Response Time with FF Experiment Disabled
        int max = 1000, min = 900;

        // Set percentage Error with FF Experiment Disabled
        int errorPercentage = 4;

        log.debug("starting payment validation");

        log.debug("beta feature is "+this.betaFeature);

        if (enableAuthorization){
            if (Long.valueOf((long) invoice.getValidationID()) == 0){
                log.debug("validation id not provided");

            }else {
                if (authorize(invoice)){
                    log.info("authorized");
                    invoice.setStatus("authorized");
                    return invoice;
                }

            }
            invoice.setStatus("not-authorized");
            return invoice;
        }else {


            // Comment this for you stable version or first deployment
            // Set here the increased response time with ff Experiment enabled
            // change "canary" to "not-bug" and vice versa to enable canary bug or not
            if (this.betaFeature && getVersion() == "not-bug") {
                max = 5000;
                min = 4900;
                errorPercentage = 95;
            }

            try {
                if (payments.size() >= 10000) {
                    log.debug("List Cleaner Started");
                    cleanList();
                    log.debug("List Cleaner Finished");
                }
            } catch (Exception e) {
                log.error("ERROR [Payment Validation] - Array List Exception");
                log.error(e.getMessage());
            }
            try {
                int msDelay = r.nextInt((max - min) + 1) + min;
                log.debug("delaying for " + msDelay + " seconds");
                Thread.sleep(msDelay);
                int errorPercentageSorted = r.nextInt((100 - 1) + 1);
                log.debug("set errorPercentage Sorted = " + errorPercentageSorted);
                // Percentage error values 0-100%
                if (errorPercentageSorted <= errorPercentage) {
                    invoice.setStatus("failed-bug");
                    log.error("ERROR [Payment Validation] - Failed to validate invoice - status: " + invoice.getStatus());
                    addToPaymentsValidated(invoice);
                    return invoice;
                }

                invoice.setStatus("verified");

                //payments.add(invoice);
                addToPaymentsValidated(invoice);
                return invoice;
            } catch (InterruptedException e) {
                log.error("ERROR [Payment Validation] - Interruption Exception: " + e.getMessage());
                invoice.setStatus("failed");
                //payments.add(invoice);
                addToPaymentsValidated(invoice);
                return invoice;
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = e.toString();
                }
                log.error("ERROR [Payment Validation] - Exception: " + errorMsg);
                invoice.setStatus("failed");
                //payments.add(invoice);
                addToPaymentsValidated(invoice);
                return invoice;
            }
        }
    }

    @JsonProperty
    public List<Payment> getPayments() {
        log.debug("beta feature is "+this.betaFeature);
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
