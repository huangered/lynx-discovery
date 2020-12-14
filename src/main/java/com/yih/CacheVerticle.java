package com.yih;

import com.google.gson.Gson;
import com.yih.pojo.RegisterRequest;
import com.yih.pojo.RegisterSvc;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
public class CacheVerticle extends AbstractVerticle {

    private Gson gson = new Gson();
    private Map<String, Set<RegisterSvc>> map = new ConcurrentHashMap<>();

    @Override
    public void start() throws Exception {
        EventBus bus = vertx.eventBus();
        bus.consumer("svc.register", this::handleRegister);

        bus.consumer("svc.unregister", this::handleUnregister);

        bus.consumer("svc.query", this::handleQuery);

        vertx.setPeriodic(5000, print -> {
            log.info("Print map {}", map);
        });
    }

    private void handleQuery(Message<String> msg) {
        log.info("svc.query {}", msg.body());
        String svcName = msg.body();
        Set<RegisterSvc> ss = map.getOrDefault(svcName, new HashSet<>());
        if (ss.isEmpty()) {
            msg.reply("");
        } else {
            RegisterSvc svc = ss.stream().findAny().get();
            msg.reply(String.format("%s:%s", svc.getReq().getUrl(), svc.getReq().getPort()));
        }
    }

    private void handleUnregister(Message<JsonObject> msg) {
        log.info("svc.unregister {}", msg.body());
        String depId = msg.body().getString("depId");
        String svcName = msg.body().getString("svcName");

        map.getOrDefault(svcName, new HashSet<>())
                .removeIf(registerSvc -> registerSvc.getHealthCheckVerticleId().equals(depId));

    }

    private void handleRegister(Message<JsonObject> msg) {
        log.info("receive msg {}", msg.body().toString());
        RegisterRequest req = gson.fromJson(msg.body().toString(), RegisterRequest.class);

        RegisterSvc svc = new RegisterSvc(req, true);

        map.putIfAbsent(svc.getReq().getName(), new HashSet<>());
        if (!map.get(svc.getReq().getName()).contains(svc)) {
            map.get(svc.getReq().getName()).add(svc);
            vertx.deployVerticle(new HealthCheckVerticle(
                    svc.getReq().getName(),
                    svc.getReq().getUrl(),
                    svc.getReq().getPort()))
                    .onSuccess(handler -> {
                        svc.setHealthCheckVerticleId(handler);
                        svc.setAlive(true);
                    });
        }
    }

    @Override
    public void stop() throws Exception {
    }
}