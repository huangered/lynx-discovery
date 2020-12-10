package com.yih;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;

public class EmptyVerticle extends AbstractVerticle {

    private WebClient webClient;

    @Override
    public void start() throws Exception {
        webClient = WebClient.create(vertx);
        vertx.setPeriodic(5000, r -> {
            System.out.println("hello world");
            webClient.get(3000, "localhost", "/register").expect(ResponsePredicate.SC_SUCCESS)
                    .send(ar -> {
                        System.out.println(ar.succeeded());
                        System.out.println("Receive: " + ar.result().bodyAsString());

                    });
        });
    }
}