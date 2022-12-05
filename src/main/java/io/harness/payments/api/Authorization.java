package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private long invoiceId;

    @NotBlank
    private String validationId;

    public Authorization(){

    }




}
