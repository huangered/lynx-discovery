package com.yih.lynx.server;

import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.server.codec.SvcDescCodec;
import com.yih.lynx.server.verticle.CacheVerticle;
import com.yih.lynx.server.verticle.LynxVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class LynxServer {

    private LynxOption option;

    public LynxServer(LynxOption option) {
        this.option = option;
    }

    public static void main(String[] argc) {
        LynxOption option = LynxOption.builder().port(3000).wait(true).build();
        new LynxServer(option).start();
    }

    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(SvcDesc.class, new SvcDescCodec());
        Future<String> f1 = vertx.deployVerticle(new LynxVerticle(option.getPort()));
        Future<String> f2 = vertx.deployVerticle(new CacheVerticle());
        if (option.isWait()) {
            CompositeFuture f = CompositeFuture.join(f1, f2);
            while (!f.isComplete()) {

            }
        }
    }

    public void stop() {
        Vertx.vertx().close();
    }


}
