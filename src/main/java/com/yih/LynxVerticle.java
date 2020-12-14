package com.yih;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LynxVerticle extends AbstractVerticle {
    Gson gson = new Gson();

    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new LynxVerticle());
        vertx.deployVerticle(new CacheVerticle());
    }

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
        log.info("Unregister {}", routingContext.getBodyAsString());
        routingContext.response().end();
    }

    private void handleRegister(RoutingContext routingContext) {
        String request = routingContext.getBodyAsString();
        log.info("handle request {}", request);

        JsonObject obj = new JsonObject(request);
        vertx.eventBus().publish("svc.register", obj);

        routingContext.response().end("end");
    }

    private void handleQuery(RoutingContext routingContext) {
        String svcName = routingContext.queryParam("svc").get(0);

        vertx.eventBus().<String>request("svc.query", svcName)
                .onSuccess(h -> {
                    routingContext.response().end(h.body());
                });
    }
}