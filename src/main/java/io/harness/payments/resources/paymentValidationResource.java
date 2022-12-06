package io.harness.payments.resources;

import com.codahale.metrics.annotation.Timed;
import io.harness.cf.client.dto.Target;
import io.harness.payments.api.Payment;
import io.harness.payments.api.PaymentValidation;
import io.harness.payments.api.Representation;
import io.harness.payments.api.Saying;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import io.harness.cf.client.api.CfClient;

@Slf4j
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class paymentValidationResource {

    private SecureRandom r = new SecureRandom();
    private final PaymentValidation paymentValidation;

    private final CfClient cfClient;

    private boolean lock = false;

    private boolean betaFeature = false;

    public paymentValidationResource(PaymentValidation paymentValidation, CfClient cfClient) {

        this.paymentValidation =  paymentValidation;
        this.cfClient = cfClient;
    }
    @Path("/validation")
    @GET
    @Timed
    public List listValidations(@QueryParam("id") Optional<String> id) {

        Target target = Target.builder()
                .name("Guest-"+getVersion())
                .attributes(new HashMap<String, Object>())
                .identifier("Guest"+getVersion())
                .build();

        //boolean result = cfClient.boolVariation("VALIDATION_BETA_EXPERIMENT", target, false);

        //log.debug(target.getName()+ " Beta Feature is : "+result);

//        if (result){
//            paymentValidation.setBetaFeature();
//        }else {
//            paymentValidation.disableBetaFeature();
//        }

        return paymentValidation.getPayments();
    }
    @Path("/validation")
    @POST
    @Timed
    public Response validate(@NotNull @Valid final Payment invoice) {

        Target target = Target.builder()
                .name("Guest-"+getVersion())
                .attributes(new HashMap<String, Object>())
                .identifier("Guest"+getVersion())
                .build();



        boolean result = false;

        try {
            result = cfClient.boolVariation("VALIDATION_BETA_EXPERIMENT", target, false);
            this.betaFeature = result;
        }catch (Exception e){
            log.warn("[Feature Flags] - ");
            result = betaFeature;
        }

//        if (lock = true){
//            result = betaFeature;
//        }else{
//            lock = true;
//            result = cfClient.boolVariation("VALIDATION_BETA_EXPERIMENT", target, false);
//            betaFeature = result;
//            lock = false;
//        }


        log.debug(target.getName()+ " Beta Feature is : "+result);

        if (result){
            paymentValidation.setBetaFeature();
        }else {
            paymentValidation.disableBetaFeature();
        }

        Payment validatedPayment = paymentValidation.validate(invoice);

        if (validatedPayment.getStatus() != "verified" && validatedPayment.getStatus() != "authorized" ){
            log.info("payment not validated");

            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                    entity(new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, validatedPayment)).type("application/json").build();
            //throw new InternalServerErrorException(validatedPayment);



            //return new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, validatedPayment);
        }
        log.debug("payment validated");
        return Response.status(HttpStatus.OK_200).
                entity(new Representation<Payment>(HttpStatus.OK_200, validatedPayment)).type("application/json").build();
        //return new Representation<Payment>(HttpStatus.OK_200, validatedPayment);
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


    @Path("/authorization")
    @POST
    @Timed
    public Response authorize(@NotNull @Valid final Payment invoice) {

        Target target = Target.builder()
                .name("Guest-"+getVersion())
                .attributes(new HashMap<String, Object>())
                .identifier("Guest"+getVersion())
                .build();



        boolean result = false;

        try {
            result = cfClient.boolVariation("AUTHORIZATION_BETA_EXPERIMENT", target, false);
            this.betaFeature = result;
        }catch (Exception e){
            log.warn("[Feature Flags] - ");
            result = betaFeature;
        }

        log.debug(target.getName()+ " Beta Feature is : "+result);

        if (result){
            //paymentValidation.setBetaFeature();
        }else {
            //paymentValidation.disableBetaFeature();
        }

        String authorization = paymentValidation.getAuthorization(invoice.getId());

        invoice.setValidationID(authorization);

        if (authorization != null ){

            log.debug("authorization generated");

            // Set here the Max and Min Response Time with FF Experiment Disabled
            int max = 1000, min = 900;
            int errorPercentage = 5;

            int msDelay = r.nextInt((max - min) + 1) + min;
            log.debug("delaying for " + msDelay + " seconds");
            try {
                Thread.sleep(msDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int errorPercentageSorted = r.nextInt((100 - 1) + 1);
            log.debug("set errorPercentage Sorted = " + errorPercentageSorted);
            // Percentage error values 0-100%
            if (errorPercentageSorted <= errorPercentage) {
                invoice.setStatus("failed-bug-"+getVersion());
                invoice.setErrorMsg("Bug Demo - version: "+getVersion());
                log.error("ERROR [Authorization Failed] - Failed to authorize invoice - error: " + invoice.getErrorMsg());

                return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                        entity(new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, invoice)).type("application/json").build();
            }

            return Response.status(HttpStatus.OK_200).
                    entity(new Representation<Payment>(HttpStatus.OK_200, invoice)).type("application/json").build();

        }else {
            log.info("authorization failed");

            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                    entity(new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, invoice)).type("application/json").build();
            //throw new InternalServerErrorException(validatedPayment);
        }


    }
}
