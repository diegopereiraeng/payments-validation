package io.harness.payments.resources;

import com.codahale.metrics.annotation.Timed;

import io.harness.payments.api.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;




@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class scanPayResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;



    public scanPayResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();

    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }




}
