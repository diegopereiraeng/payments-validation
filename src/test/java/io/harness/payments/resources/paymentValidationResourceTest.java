package io.harness.payments.resources;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.harness.cf.client.api.BaseConfig;
import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.api.FeatureFlagInitializeException;
import io.harness.cf.client.connector.HarnessConfig;
import io.harness.payments.MongoConfiguration;
import io.harness.payments.api.Payment;
import io.harness.payments.api.PaymentValidation;
import io.harness.payments.db.MongoManaged;
import io.harness.payments.scanPayApplication;
import io.harness.payments.scanPayConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
@Slf4j
@ExtendWith(DropwizardExtensionsSupport.class)
public class paymentValidationResourceTest {



    @Path("/auth/validation")
    public static class paymentValidationResource {
        private SecureRandom r = new SecureRandom();

        @GET
        public String listValidations(@QueryParam("id") Optional<String> id) {
            List<Payment> payments =  new ArrayList<Payment>();
            payments.add(new Payment(1));
            payments.add(new Payment(2));
            payments.add(new Payment(3));
            try {
                int max = 15000, min = 10000;



                log.info("starting payment validation");

                try {
                    int msDelay = r.nextInt((max - min) + 1) + min;
                    log.debug("delaying for "+msDelay+" seconds");
                    Thread.sleep(msDelay);



                } catch (InterruptedException e) {
                    log.error("ERROR [Payment Validation] - Interruption Exception: "+e.getMessage());


                }catch (Exception e){
                    String errorMsg = e.getMessage();
                    if (errorMsg == null){
                        errorMsg = e.toString();
                    }
                    log.error("ERROR [Payment Validation] - Exception: "+errorMsg);

                }
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
            }
            return "pong";
        }
    }


    private static final DropwizardClientExtension EXT = new DropwizardClientExtension(new paymentValidationResource());

    @Test
    void listValidations() throws IOException {
        final URL url = new URL(EXT.baseUri() + "/auth/validation");
        final String response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
        assertEquals("pong", response);
    }

}
