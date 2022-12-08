package io.harness.payments.db;

import com.mongodb.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import io.harness.payments.api.Authorization;
import io.harness.payments.MongoConfiguration;
import io.dropwizard.lifecycle.Managed;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.harness.payments.api.Payment;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;


import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Created by Diego Pereira on 12/05/2022.
 */
@Slf4j
public class MongoManaged implements Managed {

    private MongoClient mongo;
    private MongoDatabase db;

    public MongoManaged (MongoConfiguration mongoConfig) throws Exception {



        String uri = mongoConfig.connectionString ;
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .build();

        //uri = System.getenv("MONGO_AUTH");
        log.info("[MONGODB] - Connecting to MONGODB: "+uri);
        try{
            ConnectionString connectionString = new ConnectionString(uri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            this.mongo = MongoClients.create(settings);
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
            this.db = this.mongo.getDatabase("banking").withCodecRegistry(pojoCodecRegistry);
        }catch (Exception e){
            uri = System.getenv("MONGO_AUTH");
            log.info("[MONGODB] - Failed to connect to Mongodb using config file, trying env var:");
            log.info(uri);
            ConnectionString connectionString = new ConnectionString(uri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            this.mongo = MongoClients.create(settings);
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
            this.db = this.mongo.getDatabase("banking").withCodecRegistry(pojoCodecRegistry);
        }


//        MongoCollection<Authorization> collection = this.db.getCollection("auth", Authorization.class);
//        collection.insertOne(new Authorization(50));
//
//        Authorization auth = collection.find(eq("invoiceId", 7)).first();
//
//        if (auth == null){
//            System.out.println("Diego Null");
//        }else {
//            System.out.println("Diego "+auth.getInvoiceId());
//        }



    }

    public Authorization authorize(long invoiceId){

        log.info("Authorizing: "+invoiceId);
        MongoCollection<Authorization> collection = this.db.getCollection("auth", Authorization.class);
        Authorization auth = collection.find(eq("invoiceId", invoiceId)).first();

        if (auth == null){
            return null;
        }

        collection.deleteOne(Filters.eq("invoiceId", invoiceId));

        return auth;
    }

    public Authorization getAuthorization(long invoiceId){
        Authorization auth;
        log.info("Getting Authorization for invoiceID: "+invoiceId);
        try {
            MongoCollection<Authorization> collection = this.db.getCollection("auth", Authorization.class);
            auth = new Authorization(invoiceId);
            InsertOneResult result = collection.insertOne(auth);
            log.info("Diego "+result.getInsertedId().toString());
            return auth;
        }catch (Exception e){
            return null;
        }


        //Authorization auth = collection.find(eq("invoiceId", invoiceId)).first();
        //System.out.println("Diego "+auth.getInvoiceId());


    }


    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        this.mongo.close();
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public MongoDatabase getDb() {
        return db;
    }
}