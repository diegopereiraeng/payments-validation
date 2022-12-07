package io.harness.payments.behavior;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

//import javax.swing.text.html.parser.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

@Slf4j
public class MetricsGenerator implements Runnable {

    private SecureRandom r = new SecureRandom();

    Client client = ClientBuilder.newClient();
    public MetricsGenerator() {

    }

    private CompletableFuture<Void> createFuture(){
        Runnable runnable = () -> {

            log.debug("Staring Async Validation Task - "+Thread.currentThread().getName());

            try{
                // Validation Calls
                log.debug("Validation Calls");
                String result = client.target("http://localhost:8080"+"/auth/validation").request().get(String.class);


                if (r.nextInt(2) <= 0) {
                    // Validation Body

                    String jsonString = new JSONObject()
                            .put("id", 7)
                            .put("status", "not verified")
                            //.put("JSON3", new JSONObject().put("key1", "value1"))
                            .toString();

                    String result2 = client.target("http://localhost:8080" + "/auth/validation").request().post(Entity.json(jsonString), String.class);

                    log.debug("validation completed: " + result);
                }

            }catch (Exception e){
                //log.error("Metrics Generator Error (Validation APIs)");
            }
            log.debug("Finished Async Validation Task - "+Thread.currentThread().getName());

        };
        return CompletableFuture.runAsync(runnable);
    }

    @SneakyThrows
    @Override
    public void run() {

        log.info("Staring Async Validation Task Thread");

        try{

            /** FEATURE FLAGS **
             * Put the API Key here from your environment
             */

            /**
             * Define you target on which you would like to evaluate the featureFlag
             */
            int calls_per_minute = 60;

            for (int i = 0; i < calls_per_minute; i++) {
                //long startTime = System.nanoTime();
                try {
                    createFuture().get();
                    Thread.sleep(40000 / calls_per_minute);
                }catch (InterruptedException ex) {
                    log.error(ex.getMessage());
                    Thread.currentThread().interrupt();
                }catch (Exception e){
                    log.error("ERROR [Metric Generator] - "+e.getMessage());
                }

                //long endTime = System.nanoTime();

                //long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

            }
        } catch (Exception e) {
            log.error("Fail in generating Metric Behavior");
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            log.error(e.getLocalizedMessage());
            log.error(e.toString());
        }

    }

}
