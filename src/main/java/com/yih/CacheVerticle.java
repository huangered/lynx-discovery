package com.yih;

import com.google.gson.Gson;
import com.yih.pojo.SvcDesc;
import com.yih.pojo.SvcStatus;
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

@Slf4j
public class CacheVerticle extends AbstractVerticle {

    private final Gson gson = new Gson();
    private final Map<String, Set<SvcStatus>> map = new ConcurrentHashMap<>();

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
        Set<SvcStatus> ss = map.getOrDefault(svcName, new HashSet<>());
        if (ss.isEmpty()) {
            msg.reply("");
        } else {
            SvcStatus svc = ss.stream().findAny().get();
            msg.reply(String.format("%s:%s", svc.getDesc().getUrl(), svc.getDesc().getPort()));
        }
    }

    private void handleUnregister(Message<JsonObject> msg) {
        log.info("svc.unregister {}", msg.body());
        String depId = msg.body().getString("depId");
        String svcName = msg.body().getString("svcName");

        map.getOrDefault(svcName, new HashSet<>())
                .removeIf(svcStatus -> svcStatus.getHealthCheckVerticleId().equals(depId));

    }

    private void handleRegister(Message<SvcDesc> req) {
        log.info("receive msg {}", req.body().toString());

        SvcStatus svc = new SvcStatus(req.body(), true);

        map.putIfAbsent(svc.getDesc().getName(), new HashSet<>());
        if (!map.get(svc.getDesc().getName()).contains(svc)) {
            map.get(svc.getDesc().getName()).add(svc);
            vertx.deployVerticle(new HealthCheckVerticle(svc))
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