package io.harness.payments.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
@Slf4j
public class PaymentTest {
    private static final ObjectMapper MAPPER = newObjectMapper();

    private SecureRandom r = new SecureRandom();

    @Test
    void seralizesToJSON() throws Exception {
        final Payment payment = new Payment(23216);
        payment.setVersion("stable");
        payment.setErrorMsg("Testing Error Msg");
        payment.setStatus("failed-bug");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(getClass().getResource("/fixtures/Payment.json"), Payment.class));

        try {

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

            log.error("Error to seralizesToJSON (just kidding)");


        }catch (Exception e){
            log.error("Error:"+e.getMessage());
        }

        assertThat(MAPPER.writeValueAsString(payment)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final Payment payment = new Payment(23216);
        payment.setVersion("stable");
        payment.setErrorMsg("Testing Error Msg");
        payment.setStatus("failed-bug");
        assertThat(MAPPER.readValue(getClass().getResource("/fixtures/Payment.json"), Payment.class))
                .isEqualTo(payment);
    }
}
