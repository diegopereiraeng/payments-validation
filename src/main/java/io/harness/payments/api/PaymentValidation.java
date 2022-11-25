package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;


public abstract class PaymentValidation {
    List payments =  new ArrayList();

    public Payment validate(Payment invoice){
        if (payments.size() >= 1000){
            this.payments = payments.subList(100,payments.size()-1);
        }
        try {
            Thread.sleep(3000);
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



}
