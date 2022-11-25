package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payment {
    private long id;

    private String status;

    public Payment() {
        // Jackson deserialization
    }

    public Payment(long id) {
        this.id = id;
        this.status = "not-verified";
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
        return status;
    }
}
