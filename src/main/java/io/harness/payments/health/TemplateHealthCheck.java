package io.harness.payments.health;

import com.codahale.metrics.health.HealthCheck;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateHealthCheck extends HealthCheck {
    private final String template;

    public TemplateHealthCheck(String template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            log.error("Health Check Failed");
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
}
