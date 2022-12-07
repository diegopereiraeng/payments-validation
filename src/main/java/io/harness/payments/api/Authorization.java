package io.harness.payments.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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


    public Authorization(){

    }
    public Authorization(long invoiceId){
        this.invoiceId = invoiceId;
        this.errorMsg = "";
    }

//    public String getId(){
//        return id;
//    }

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
