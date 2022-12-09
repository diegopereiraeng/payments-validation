package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.harness.payments.db.MongoManaged;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public abstract class PaymentValidation {

    //private JacksonDBCollection<Payment, String> paymentsDAO;

    MongoManaged mongodb;

    public PaymentValidation(MongoManaged mongodb){

        try {
            this.mongodb = mongodb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    List<Payment> payments =  new ArrayList<Payment>();

    private Map<Double, Long> authorizations
            = new ConcurrentHashMap<Double, Long>();

    private boolean payLock = false;

    private boolean authorizationLock = false;

    private boolean betaFeature = false;

    private boolean authBetaFeature = false;

    public void setAuthBetaFeature(){
        this.authBetaFeature = true;
    }

    public void disableAuthBetaFeature(){
        this.authBetaFeature = false;
    }

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

    public String getAuthorization(long invoiceID){

        //log.info("getting authorization");

        Authorization auth;
        log.info("Getting Authorization for invoiceID: "+invoiceID);
        try {

            // Set here the Max and Min Response Time with FF Experiment Disabled
            int max = 1000, min = 900;
            int errorPercentage = 5;

            if (this.authBetaFeature && getVersion().equals("canary")) {
                max = 4000;
                min = 3900;
                errorPercentage = 95;
            }

            int msDelay = r.nextInt((max - min) + 1) + min;
            log.debug("delaying for " + msDelay + " seconds");
            try {
                Thread.sleep(msDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int errorPercentageSorted = r.nextInt((100 - 1) + 1);
            log.debug("set errorPercentage Sorted = " + errorPercentageSorted);
            // Percentage error values 0-100%
            if (errorPercentageSorted <= errorPercentage) {

                log.error("ERROR [Authorization Failed] - Failed to authorize invoice : " + invoiceID);

                return null;
            }


            auth = mongodb.getAuthorization(invoiceID);
            return auth.getValidationId();
        }catch (Exception e){
            log.error("[Authorization] - message: "+e.getMessage());
            return null;
        }

    }

    public Authorization authorize(Payment invoice){

        Authorization accepted = mongodb.authorize(invoice.getId());

        if (accepted == null){
            accepted = new Authorization(invoice.getId());
            accepted.setErrorMsg("ERROR [Authorization] - Mongodb");
        }

        //log.info("Diego");
        accepted.setVersion(getVersion());
        return accepted;
    }

    public Payment validate(Payment invoice){

        // Set here the Max and Min Response Time with FF Experiment Disabled
        int max = 700, min = 500;
        int msDelay = r.nextInt((max - min) + 1) + min;

        // Set percentage Error with FF Experiment Disabled
        int errorPercentage = 2;

        log.debug("starting payment validation");

        log.debug("beta feature is "+this.betaFeature);

        String validationID = invoice.getValidationID();

        if (validationID == null){
            validationID = "";
        }

        if (enableAuthorization && !(validationID.equals("")) && !(validationID.equals("load"))){

            log.info("[Payment Validation] Authorizing id: '"+invoice.getValidationID()+"'");
            String errorMsg = "";
            if (this.betaFeature && getVersion().equals("canary")) {
                max = 5000;
                min = 4900;
                errorPercentage = 95;


                msDelay = r.nextInt((max - min) + 1) + min;

            }
            try {
                Thread.sleep(msDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int errorPercentageSorted = r.nextInt((100 - 1) + 1);
            log.debug("set errorPercentage Sorted = " + errorPercentageSorted);
            // Percentage error values 0-100%
            if (errorPercentageSorted <= errorPercentage) {
                invoice.setStatus("failed-bug");
                log.error("ERROR [Payment Validation] - Failed to validate invoice - status: " + invoice.getStatus());
                addToPaymentsValidated(invoice);
                return invoice;
            }

            Authorization auth = authorize(invoice);
            String error = auth.getErrorMsg();
            if (error != null){
                if (error.equals("")){
                    log.debug("authorized");
                    invoice.setStatus("authorized");

                    return invoice;
                }
                errorMsg = "ERROR [Authorization] - Unknown Error";
            }else {
                errorMsg = auth.getErrorMsg();
            }
            invoice.setErrorMsg(errorMsg);
            invoice.setStatus("not-authorized");
            return invoice;
        }else {


            log.debug("[Payment Validation] Skipped auth for load generation");
            // Comment this for you stable version or first deployment
            // Set here the increased response time with ff Experiment enabled
            // change "canary" to "not-bug" and vice versa to enable canary bug or not
            if (this.betaFeature && getVersion().equals("canary")) {
                max = 5000;
                min = 4900;
                errorPercentage = 95;
                msDelay = r.nextInt((max - min) + 1) + min;
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
