package com.yih.lynx.server.verticle;

import com.yih.lynx.server.LynxOption;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LynxServerVerticle extends AbstractVerticle {

    private LynxOption option;

    public LynxServerVerticle(LynxOption option) {
        this.option = option;
    }

    @Override
    public void start() {
        vertx.deployVerticle(new LynxVerticle(option.getPort()));
        vertx.deployVerticle(new CacheVerticle());
    }

    @Override
    public void stop() {
        log.info("stop the lynx server");
    }
}
