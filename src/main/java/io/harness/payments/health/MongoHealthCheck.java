package io.harness.payments.health;

import io.harness.payments.db.MongoManaged;
import com.codahale.metrics.health.HealthCheck;

/**
 * Created by Diego Pereira on 12/05/2022.
 */
public class MongoHealthCheck extends HealthCheck {

    private MongoManaged mongo;

    public MongoHealthCheck(MongoManaged mongoManaged) {
        this.mongo = mongoManaged;
    }

    @Override
    protected Result check() throws Exception {
        mongo.getDb();
        return Result.healthy();
    }

}
