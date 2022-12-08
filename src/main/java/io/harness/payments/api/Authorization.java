package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;
import org.mongojack.Id;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {

//    @Id
//    private String id = UUID.randomUUID().toString();

    @NotNull
    private long invoiceId;
    @Id
    @NotNull
    private String validationId = UUID.randomUUID().toString();

    @NotNull
    private String errorMsg;

    private String version;

    public Authorization(){

    }
    public Authorization(long invoiceId){
        this.invoiceId = invoiceId;
        this.errorMsg = "";
        this.version = "stable";
    }


    @JsonProperty
    public String getVersion() {
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


    public String getValidationId(){
        return validationId;
    }

//    public void setValidationId(String validationId){
//        this.validationId = validationId;
//    }

    public long getInvoiceId(){
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId){
        this.invoiceId = invoiceId;
    }


}
