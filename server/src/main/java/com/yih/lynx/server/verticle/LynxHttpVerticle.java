package com.yih.lynx.server.verticle;

import com.google.gson.Gson;
import com.yih.lynx.core.SvcDesc;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LynxHttpVerticle extends AbstractVerticle {

    private final int port;
    private Gson gson = new Gson();

    public LynxHttpVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.post().handler(BodyHandler.create());
        router.get().handler(BodyHandler.create());

        router.get("/").handler(this::handleEcho);

        router.post("/register")
                .handler(this::handleRegister);

        router.post("/unregister")
                .handler(this::handleUnregister);

        router.get("/query")
                .handler(this::handleQuery);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port).onComplete(r-> {
                    if(r.succeeded()) {
                        log.info("startup status: success");
                    }else {
                        log.info("startup status: fail");
                    }
        });
    }

    @Override
    public void stop() throws Exception {
        log.info("stop the web server");
    }

    private void handleEcho(RoutingContext routingContext) {
        log.info("echo");
        routingContext.response().end("echo");
    }

    private void handleUnregister(RoutingContext routingContext) {
        SvcDesc request = gson.fromJson(routingContext.getBodyAsString(), SvcDesc.class);
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
                    routingContext.response().setStatusCode(404).end();
                });
    }
}