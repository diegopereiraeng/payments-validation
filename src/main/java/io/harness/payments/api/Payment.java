package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Payment implements Serializable {
    private long id;

    private String status;

    private String validationID;

    private String errorMsg;

    public Payment() {
        // Jackson deserialization
    }

    public Payment(long id) {
        this.id = id;
        this.status = "not-verified";
        this.validationID = "";
        this.errorMsg = "";
    }

    @JsonProperty
    public String getErrorMsg() {
        return errorMsg;
    }

    @JsonProperty
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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
    public String setValidationID(String validationID) {
        this.validationID = validationID;
        return this.validationID;
    }

    @JsonProperty
    public String getValidationID() {
        return validationID;
    }

}
