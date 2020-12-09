package com.yih;

import io.vertx.core.Vertx;

public class HttpConnect {
    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new EmptyVerticle());
        vertx.createHttpServer().requestHandler(r -> {
            System.out.println(r.uri());
            System.out.println(r.headers());
            r.response().end("end");
        }).listen(3000);
    }
}