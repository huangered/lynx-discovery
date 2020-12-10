package com.yih;

import com.google.gson.Gson;
import com.yih.pojo.RegisterRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.print.attribute.standard.RequestingUserName;
import java.util.*;
import java.util.function.BiFunction;

public class LynxVerticle extends AbstractVerticle {
    Map<String, Set<RegisterRequest>> map = new HashMap<>();
    Gson gson = new Gson();

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.post("/register")
                .handler(BodyHandler.create())
                .handler(this::handleRegister);

        router.get("/query")
                .handler(BodyHandler.create())
                .handler(this::handleQuery);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(3000);
    }

    private void handleRegister(RoutingContext routingContext) {
        String request = routingContext.getBodyAsString();
        System.out.println(request);
        RegisterRequest req = gson.fromJson(request, RegisterRequest.class);
        // deploy new health check verticle.
        Set<RegisterRequest> rr = new HashSet<>();
        rr.add(req);
        map.merge(req.getName(), rr, (pre, cur) -> {
            pre.addAll(cur);
            return pre;
        });
        System.out.println(map);

        routingContext.response().end("end");
    }

    private void handleQuery(RoutingContext routingContext) {
        String svcName = routingContext.queryParam("svc").get(0);

        Optional<RegisterRequest> urlOp = map.get(svcName).stream().findAny();
        if (urlOp.isPresent()) {
            RegisterRequest url = urlOp.get();
            routingContext.response().end(url.getUrl() + ":" + url.getPort());
        } else {
            routingContext.response().end();
        }
    }

    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new LynxVerticle());
    }
}