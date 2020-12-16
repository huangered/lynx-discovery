package com.yih.lynx.server;

import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.server.codec.SvcDescCodec;
import com.yih.lynx.server.verticle.CacheVerticle;
import com.yih.lynx.server.verticle.LynxVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class LynxServer {

    public static void main(String[] argc) {
        new LynxServer().start(3000,true);
    }

    public void start(int port, boolean wait) {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(SvcDesc.class, new SvcDescCodec());
        Future<String> f1 = vertx.deployVerticle(new LynxVerticle(port));
        Future<String> f2 = vertx.deployVerticle(new CacheVerticle());
        if (wait) {
            CompositeFuture f = CompositeFuture.join(f1, f2);
            while (!f.isComplete()) {

            }
        }
    }

    public void stop() {
        Vertx.vertx().close();
    }
}
