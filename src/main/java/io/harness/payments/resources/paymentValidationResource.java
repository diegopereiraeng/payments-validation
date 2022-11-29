package io.harness.payments.resources;

import com.codahale.metrics.annotation.Timed;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
@Path("/validation")
@Produces(MediaType.APPLICATION_JSON)
public class paymentValidationResource {
    private final PaymentValidation paymentValidation;

    public paymentValidationResource(PaymentValidation paymentValidation) {

        this.paymentValidation =  paymentValidation;
    }

    @GET
    @Timed
    public List listValidations(@QueryParam("id") Optional<String> id) {

        return paymentValidation.getPayments();
    }

    @POST
    @Timed
    public Response validate(@NotNull @Valid final Payment invoice) {
        Payment validatedPayment = paymentValidation.validate(invoice);

        if (validatedPayment.getStatus() != "verified" ){
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
}
