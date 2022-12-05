package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Payment implements Serializable {
    private long id;

    private String status;

    private double validationID;

    public Payment() {
        // Jackson deserialization
    }

    public Payment(long id) {
        this.id = id;
        this.status = "not-verified";
        this.validationID = 0;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    @JsonProperty
    public String setStatus(String status) {
        this.status = status;
        return this.status;
    }

    @JsonProperty
    public double setValidationID(double validationID) {
        this.validationID = validationID;
        return this.validationID;
    }

    @JsonProperty
    public double getValidationID() {
        return validationID;
    }

}
