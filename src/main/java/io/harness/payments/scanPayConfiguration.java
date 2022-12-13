package io.harness.payments;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tranchitam.dropwizard.swagger.configurations.SwaggerBundleConfiguration;

import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;



public class scanPayConfiguration extends Configuration {


    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;


    @NotNull
    public MongoConfiguration mongo = new MongoConfiguration();

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }
}
