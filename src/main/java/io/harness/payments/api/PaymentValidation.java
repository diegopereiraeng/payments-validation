package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
public abstract class PaymentValidation {
    List payments =  new ArrayList();

    private SecureRandom r = new SecureRandom();

    public Payment validate(Payment invoice){
        int max = 1000, min = 900;

        log.debug("starting payment validation");

        try{
            if (payments.size() >= 1000){
                this.payments = payments.subList(100,payments.size()-100);
            }
        }catch (Exception e){
            log.error("Array List Exception");
            log.error(e.getMessage());
        }

        try {

            int msDelay = r.nextInt((max - min) + 1) + min;
            log.debug("delaying for "+msDelay+" seconds");
            Thread.sleep(msDelay);
            int newNumber = r.nextInt((100 - 1) + 1) ;
            log.debug("Lucky Number = "+ newNumber);
            if (newNumber < 5) {

                log.error("ERROR [Payment Validation] - Failed to validate invoice");
                invoice.setStatus("failed-bug");
                return invoice;
            }

            invoice.setStatus("verified");
            payments.add(invoice);
            return invoice;
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            invoice.setStatus("failed");
            payments.add(invoice);
            return invoice;
        }catch (Exception e){
            log.error(e.getMessage());
            invoice.setStatus("failed");
            payments.add(invoice);
            return invoice;
        }
    }

    @JsonProperty
    public List getPayments() {
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
