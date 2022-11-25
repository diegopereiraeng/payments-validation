package io.harness.payments.resources;

import com.codahale.metrics.annotation.Timed;
import io.harness.payments.api.Payment;
import io.harness.payments.api.PaymentValidation;
import io.harness.payments.api.Representation;
import io.harness.payments.api.Saying;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
@Path("/validation")
@Produces(MediaType.APPLICATION_JSON)
public class paymentValidationResource {
    private final PaymentValidation paymentValidation;

    public paymentValidationResource(PaymentValidation paymentValidation) {

        this.paymentValidation =  paymentValidation;
    }

    @GET
    @Timed
    public List sayHello(@QueryParam("id") Optional<String> id) {

        return paymentValidation.getPayments();
    }

    @POST
    @Timed
    public Representation<Payment> validate(@NotNull @Valid final Payment invoice) {
        Payment validatedPayment = paymentValidation.validate(invoice);
        if (validatedPayment.getStatus() != "verified" ){
            return new Representation<Payment>(HttpStatus.INTERNAL_SERVER_ERROR_500, validatedPayment);
        }
        return new Representation<Payment>(HttpStatus.OK_200, validatedPayment);
    }
}
