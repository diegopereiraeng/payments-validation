
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import io.harness.cf.client.api.CfClient;

@Slf4j
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class authorizationResource {
    private final PaymentValidation paymentValidation;

    private final CfClient cfClient;

    private boolean lock = false;

    private boolean betaFeature = false;

    public authorizationResource(PaymentValidation paymentValidation, CfClient cfClient) {

        this.paymentValidation =  paymentValidation;
        this.cfClient = cfClient;
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

        if (authorization == null){
            log.info("authorization failed");
            invoice.setErrorMsg("authorization failed - could not get an authorization");
            invoice.setStatus("failed");

            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                    entity(new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, invoice)).type("application/json").build();
        }

        invoice.setValidationID(authorization);

        if (paymentValidation.authorize(invoice) != null ){

            log.debug("authorization generated");
            return Response.status(HttpStatus.OK_200).
                    entity(new Representation<Payment>(HttpStatus.OK_200, invoice)).type("application/json").build();

        }else {
            log.info("authorization failed");
            invoice.setErrorMsg("authorization failed - invoice id: "+invoice.getId()+" couldn't be validated");
            invoice.setStatus("failed");
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                    entity(new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, invoice)).type("application/json").build();
            //throw new InternalServerErrorException(validatedPayment);
        }


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
