package io.harness.payments;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.harness.cf.client.api.BaseConfig;
import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.api.FeatureFlagInitializeException;
import io.harness.cf.client.connector.HarnessConfig;
import io.harness.cf.client.connector.HarnessConnector;
import io.harness.payments.api.Payment;
import io.harness.payments.api.PaymentValidation;
import io.harness.payments.behavior.BehaviorGenerator;
import io.harness.payments.health.TemplateHealthCheck;
import io.harness.payments.resources.paymentValidationResource;
import io.harness.payments.resources.scanPayResource;

//import io.prometheus.client.CollectorRegistry;
//import io.prometheus.client.dropwizard.DropwizardExports;
//import io.prometheus.client.exporter.MetricsServlet;

import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerBundle;
import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerHttpFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterRegistration;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
@Slf4j
public class scanPayApplication extends Application<scanPayConfiguration> {


    public static final BehaviorGenerator behaviorGenerator =
            new BehaviorGenerator();

    public static void main(final String[] args) throws Exception {
        new scanPayApplication().run(args);
    }

    @Override
    public String getName() {
        return "scanPay";
    }

    @Override
    public void initialize(final Bootstrap<scanPayConfiguration> bootstrap) {
        bootstrap.addBundle(new MicrometerBundle());
    }

    @Override
    public void run(final scanPayConfiguration configuration,
                    final Environment environment) {
        final scanPayResource resource = new scanPayResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );

        // Feature Flags Initialization
        // diegopereiraeng env by default
        String apiKey = System.getProperty("FF_API_KEY", "97ef6f1a-52bc-4790-a6db-81b61fe3ef10");
        // Connector Config
        HarnessConfig connectorConfig = HarnessConfig.builder().build();


        // Create Options
        BaseConfig options = BaseConfig.builder()
                .pollIntervalInSeconds(60)
                .streamEnabled(true)
                .analyticsEnabled(true)
                .build();

        // Create the client
        CfClient cfClient = new CfClient(new HarnessConnector(apiKey, connectorConfig), options);
        //CfClient cfClient = new CfClient(apiKey, options);
        try {
            cfClient.waitForInitialization();
        } catch (InterruptedException e) {
            log.error("[Feature Flags] - Init Error: "+e.getMessage());
        } catch (FeatureFlagInitializeException e) {
            log.error("[Feature Flags] - Init Error: "+e.getMessage());
        }

        final paymentValidationResource payResource = new paymentValidationResource(new PaymentValidation(){},cfClient);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());

        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(payResource);
//        registerMetrics(environment);
        FilterRegistration.Dynamic micrometerFilter = environment.servlets().addFilter("MicrometerHttpFilter", new MicrometerHttpFilter());
        micrometerFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");



        behaviorGenerator.init();

    }

//    private void registerMetrics(Environment environment) {
//        CollectorRegistry collectorRegistry = new CollectorRegistry();
//        collectorRegistry.register(new DropwizardExports(environment.metrics()));
//        environment.admin().addServlet("metrics", new MetricsServlet(collectorRegistry))
//                .addMapping("/metrics");
//    }

}
