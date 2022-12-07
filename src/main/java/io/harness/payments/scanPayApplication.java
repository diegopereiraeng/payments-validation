package io.harness.payments;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
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
import io.harness.payments.resources.authorizationResource;
import io.harness.payments.resources.paymentValidationResource;
import io.harness.payments.resources.scanPayResource;

import io.harness.payments.db.MongoManaged;
import io.harness.payments.health.MongoHealthCheck;

//import io.prometheus.client.CollectorRegistry;
//import io.prometheus.client.dropwizard.DropwizardExports;
//import io.prometheus.client.exporter.MetricsServlet;

import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerBundle;
import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerHttpFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterRegistration;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.eclipse.jetty.servlets.CrossOriginFilter;


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

        // Mongo
        MongoManaged mongoManaged;
        try{
            mongoManaged = new MongoManaged(configuration.mongo);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        environment.lifecycle().manage(mongoManaged);
        environment.healthChecks().register("MongoHealthCheck", new MongoHealthCheck(mongoManaged));


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
        //CfClient cfClient = new CfClient(new HarnessConnector(apiKey, connectorConfig), options);
        CfClient cfClient = new CfClient(apiKey, options);
        try {
            cfClient.waitForInitialization();
        } catch (InterruptedException e) {
            log.error("[Feature Flags] - Init Error: "+e.getMessage());
        } catch (FeatureFlagInitializeException e) {
            log.error("[Feature Flags] - Init Error: "+e.getMessage());
        }

        final PaymentValidation payValidation = new PaymentValidation(mongoManaged){};

        final paymentValidationResource payResource = new paymentValidationResource(payValidation,cfClient);

        final authorizationResource authResource = new authorizationResource(payValidation,cfClient);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());

        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(payResource);
        //environment.jersey().register(authResource);
//        registerMetrics(environment);
        FilterRegistration.Dynamic micrometerFilter = environment.servlets().addFilter("MicrometerHttpFilter", new MicrometerHttpFilter());
        micrometerFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // DO NOT pass a preflight request to down-stream auth filters
        // unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());

        // Enable CORS headers - END

        behaviorGenerator.init();

    }

//    private void registerMetrics(Environment environment) {
//        CollectorRegistry collectorRegistry = new CollectorRegistry();
//        collectorRegistry.register(new DropwizardExports(environment.metrics()));
//        environment.admin().addServlet("metrics", new MetricsServlet(collectorRegistry))
//                .addMapping("/metrics");
//    }

}
