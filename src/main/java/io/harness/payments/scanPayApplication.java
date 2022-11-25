package io.harness.payments;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.harness.payments.api.Payment;
import io.harness.payments.api.PaymentValidation;
import io.harness.payments.health.TemplateHealthCheck;
import io.harness.payments.resources.paymentValidationResource;
import io.harness.payments.resources.scanPayResource;

//import io.prometheus.client.CollectorRegistry;
//import io.prometheus.client.dropwizard.DropwizardExports;
//import io.prometheus.client.exporter.MetricsServlet;

import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerBundle;
import io.github.maksymdolgykh.dropwizard.micrometer.MicrometerHttpFilter;
import javax.servlet.FilterRegistration;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class scanPayApplication extends Application<scanPayConfiguration> {

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
        final paymentValidationResource payResource = new paymentValidationResource(new PaymentValidation(){});
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(payResource);
//        registerMetrics(environment);
        FilterRegistration.Dynamic micrometerFilter = environment.servlets().addFilter("MicrometerHttpFilter", new MicrometerHttpFilter());
        micrometerFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

//    private void registerMetrics(Environment environment) {
//        CollectorRegistry collectorRegistry = new CollectorRegistry();
//        collectorRegistry.register(new DropwizardExports(environment.metrics()));
//        environment.admin().addServlet("metrics", new MetricsServlet(collectorRegistry))
//                .addMapping("/metrics");
//    }

}
