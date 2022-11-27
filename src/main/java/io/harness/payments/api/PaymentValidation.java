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

        log.info("starting payment validation");

        if (payments.size() >= 1000){
            this.payments = payments.subList(100,payments.size()-1);
        }
        try {

            int msDelay = r.nextInt((max - min) + 1) + min;
            log.info("delaying for "+msDelay+" seconds");
            Thread.sleep(msDelay);

            if (r.nextInt((100 - 1) + 1) < 5) {

                log.error("ERROR [Payment Validation] - Failed to validate invoice");
                invoice.setStatus("failed-bug");
                return invoice;
            }

            invoice.setStatus("verified");
            payments.add(invoice);
            return invoice;
        } catch (InterruptedException e) {
            invoice.setStatus("failed");
            payments.add(invoice);
            return invoice;
        }catch (Exception e){
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
