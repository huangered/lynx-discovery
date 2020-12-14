package com.yih;

import com.google.gson.Gson;
import com.yih.pojo.RegisterRequest;
import com.yih.pojo.RegisterSvc;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

import javax.print.attribute.standard.RequestingUserName;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
public class LynxVerticle extends AbstractVerticle {
    Map<String, Set<RegisterSvc>> map = new HashMap<>();
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
        log.info("Unregister {}", routingContext.getBodyAsString());
        routingContext.response().end();
    }

    private void handleRegister(RoutingContext routingContext) {
        String request = routingContext.getBodyAsString();
        log.info("request {}", request);

        RegisterRequest req = gson.fromJson(request, RegisterRequest.class);
        // deploy new health check verticle.
        Set<RegisterSvc> rr = new HashSet<>();
        rr.add(new RegisterSvc(req, true));
        map.merge(req.getName(), rr, (pre, cur) -> {
            pre.addAll(cur);
            return pre;
        });
        log.info("Map {}", map);
        routingContext.response().end("end");
    }

    private void handleQuery(RoutingContext routingContext) {
        String svcName = routingContext.queryParam("svc").get(0);

        Optional<RegisterSvc> urlOp = map.get(svcName).stream().findAny();
        if (urlOp.isPresent()) {
            RegisterSvc url = urlOp.get();
            routingContext.response().end(url.getReq().getUrl() + ":" + url.getReq().getPort());
        } else {
            routingContext.response().end();
        }
    }

    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new LynxVerticle());
    }
}