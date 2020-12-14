package com.yih;

import com.google.gson.Gson;
import com.yih.codec.SvcDescCodec;
import com.yih.pojo.SvcDesc;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LynxVerticle extends AbstractVerticle {

    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(SvcDesc.class, new SvcDescCodec());
        vertx.deployVerticle(new LynxVerticle());
        vertx.deployVerticle(new CacheVerticle());
    }

    Gson gson = new Gson();

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.post().handler(BodyHandler.create());
        router.get().handler(BodyHandler.create());

        router.post("/register")
                .handler(this::handleRegister);

        router.post("/unregister")
                .handler(this::handleUnregister);

        router.get("/query")
                .handler(this::handleQuery);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(3000);

    }

    private void handleUnregister(RoutingContext routingContext) {
        JsonObject request = routingContext.getBodyAsJson();
        log.info("Unregister {}", request);

        vertx.eventBus().publish("svc.unregister", request);

        routingContext.response().end("ok");
    }

    private void handleRegister(RoutingContext routingContext) {
        SvcDesc request = gson.fromJson(routingContext.getBodyAsString(), SvcDesc.class);
        log.info("handle request {}", request);

        vertx.eventBus().publish("svc.register", request);

        routingContext.response().end("ok");
    }

    private void handleQuery(RoutingContext routingContext) {
        String svcName = routingContext.queryParam("svc").get(0);

        vertx.eventBus().<String>request("svc.query", svcName)
                .onSuccess(h -> {
                    routingContext.response().end(h.body());
                })
                .onFailure(h -> {
                    routingContext.response().setStatusCode(500).end();
                });
    }
}