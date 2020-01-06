package com.rubiconproject.dmp.vertxstart.conf;

import com.rubiconproject.dmp.vertxstart.models.*;
import com.rubiconproject.dmp.vertxstart.verticles.*;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.rubiconproject.dmp.vertxstart.conf")
public class SpringConfiguration
{
    @Bean
    public Vertx vertx()
    {
        VertxOptions vertxOptions = new VertxOptions();
        return Vertx.vertx(vertxOptions);
    }

    @Bean
    public UserMessageProducerVerticle userMessageProducerVerticle(Vertx vertx)
    {
        UserMessageProducerVerticle verticle = new UserMessageProducerVerticle();

        vertx.eventBus().registerDefaultCodec(UserMessage.class, new UserMessageCodec());
        vertx.deployVerticle(verticle);

        return verticle;
    }

    @Bean
    public ProcessorVerticle processorVerticle(Vertx vertx)
    {
        ProcessorVerticle verticle = new ProcessorVerticle();
        vertx.deployVerticle(verticle);

        return verticle;
    }

/*    @Bean
    public ProcessorVerticleRXjava processorVerticleRX(Vertx vertx)
    {
        ProcessorVerticleRXjava verticle = new ProcessorVerticleRXjava();
        vertx.deployVerticle(verticle);

        return verticle;
    }*/

/*
    @Bean
    public ProcessorVerticleRXUpdate processorVerticleRX(Vertx vertx)
    {
        ProcessorVerticleRXUpdate verticle = new ProcessorVerticleRXUpdate();
        vertx.deployVerticle(verticle);

        return verticle;
    }*/

    @Bean
    public AerospikeVerticle aerospikeVerticle(Vertx vertx)
    {
        AerospikeVerticle verticle = new AerospikeVerticle();

        vertx.eventBus().registerDefaultCodec(AerospikeRequest.class, new AerospikeRequestCodec());
        vertx.eventBus().registerDefaultCodec(AerospikeBatchRequests.class, new AerospikeBatchRequestsCodec());
        vertx.eventBus().registerDefaultCodec(AerospikeRecord.class, new AerospikeRecordCodec());

        vertx.deployVerticle(verticle);

        return verticle;
    }

    @Bean
    public EdgeUpdateVerticle edgeUpdateVerticle(Vertx vertx)
    {
        EdgeUpdateVerticle verticle = new EdgeUpdateVerticle();

        vertx.eventBus().registerDefaultCodec(EdgeUpdateMessage.class, new EdgeUpdateMessageCodec());

        vertx.deployVerticle(verticle);

        return verticle;
    }
}
