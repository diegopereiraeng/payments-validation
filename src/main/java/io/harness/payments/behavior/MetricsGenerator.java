package io.harness.payments.behavior;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

//import javax.swing.text.html.parser.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

@Slf4j
public class MetricsGenerator implements Runnable {


    Client client = ClientBuilder.newClient();
    public MetricsGenerator() {

    }

    private CompletableFuture<Void> createFuture(){
        Runnable runnable = () -> {

            log.info("Staring Async Validation Task - "+Thread.currentThread().getName());

            try{
                // Banking Calls
                log.info("Validation Calls");
                String result = client.target("http://localhost:8080"+"/validation").request().get(String.class);


                // Validation Body

                String jsonString = new JSONObject()
                        .put("id", 7)
                        .put("status", "not verified")
                        //.put("JSON3", new JSONObject().put("key1", "value1"))
                        .toString();

                String result2 = client.target("http://localhost:8080"+"/validation").request().post( Entity.json( jsonString ),String.class);

                log.info("validation completed: "+result);


            }catch (Exception e){
                log.error("Metrics Generator Error (Validation APIs)");
            }
            log.info("Finished Async Validation Task - "+Thread.currentThread().getName());

        };
        return CompletableFuture.runAsync(runnable);
    }

    @SneakyThrows
    @Override
    public void run() {

        try{

            /** FEATURE FLAGS **
             * Put the API Key here from your environment
             */



            /**
             * Define you target on which you would like to evaluate the featureFlag
             */
            int calls_per_minute = 40;

            for (int i = 0; i < calls_per_minute; i++) {

                try {

                    createFuture().get();

                }catch (Exception e){
                    log.error("Payments Generator Error");
                }

                Thread.sleep(55000 / calls_per_minute);
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Fail in generating Metric Behavior");
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            log.error(e.getLocalizedMessage());
            log.error(e.toString());
        }

    }

}
