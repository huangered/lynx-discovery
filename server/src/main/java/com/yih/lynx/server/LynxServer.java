package com.yih.lynx.server;

import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.server.codec.SvcDescCodec;
import com.yih.lynx.server.verticle.LynxServerVerticle;
import io.vertx.core.Vertx;

public class LynxServer {

    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(SvcDesc.class, new SvcDescCodec());

        LynxOption option = LynxOption.builder().port(3000).build();

        vertx.deployVerticle(new LynxServerVerticle(option));
    }
}
