package io.harness.payments;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Diego Pereira on 12/05/2022.
 */
public class MongoConfiguration {

    @NotNull
    public String connectionString;

}
