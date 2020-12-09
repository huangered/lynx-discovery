package com.yih;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;

public class EmptyVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.setPeriodic(5000, r -> {
            System.out.println("hello world");
        });
    }
}
