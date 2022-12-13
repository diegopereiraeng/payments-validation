package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Payment implements Serializable {
    private long id;

    private String status;

    private String validationID;

    private String errorMsg;

    private String version;


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
    public String getVersion() {
        if (version == null){
            return "";
        }
        return version;
    }

    @JsonProperty
    public void setVersion(String version) {
        this.version = version;
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

    @Override
    public boolean equals(Object obj) {
        Payment pay = (Payment) obj;
        boolean result = false;
        if (pay.getId() == this.getId() && pay.getStatus().equals(this.getStatus()) && pay.getErrorMsg().equals(this.getErrorMsg()) && pay.getVersion().equals(this.getVersion())){
            result = true;
        }

        return result;
    }
}
