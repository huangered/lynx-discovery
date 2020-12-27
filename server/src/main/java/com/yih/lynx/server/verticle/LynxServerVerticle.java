package com.yih.lynx.server.verticle;

import com.yih.lynx.server.LynxConfig;
import com.yih.lynx.server.LynxOption;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LynxServerVerticle extends AbstractVerticle {

    private LynxOption option;

    private LynxConfig config;

    public LynxServerVerticle(LynxOption option) {
        this.option = option;
        this.config = new LynxConfig();
    }

    @Override
    public void start() {
        vertx.deployVerticle(new LynxHttpVerticle(option.getPort()));
        vertx.deployVerticle(new CacheVerticle());
        vertx.deployVerticle(new PeerVerticle(config.getHosts()));
    }

    @Override
    public void stop() {
        log.info("stop the lynx server");
    }
}
