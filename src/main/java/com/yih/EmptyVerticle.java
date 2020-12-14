package com.yih;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmptyVerticle extends AbstractVerticle {

    private WebClient webClient;

    private Long id;

    @Override
    public void start() throws Exception {
        webClient = WebClient.create(vertx);

        id = vertx.setPeriodic(1000, r -> {
            log.info("hello world");

        });
        log.info("start timer id: {}", id);
    }

    @Override
    public void stop() throws Exception {
        log.info("stop timer: {}", id);
        vertx.cancelTimer(id);
    }
}