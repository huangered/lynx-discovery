package com.yih.lynx.server.verticle;

import com.google.gson.Gson;
import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.core.SvcStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CacheVerticle extends AbstractVerticle {

    private final Gson gson = new Gson();
    private final Map<String, Set<SvcStatus>> map = new HashMap<>();

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
            msg.fail(404, "not found");
        } else {
            Optional<SvcStatus> svc = ss.stream()
                    .filter(SvcStatus::isAlive)
                    .findAny();
            if (svc.isPresent()) {
                msg.reply(gson.toJson(svc.get().getDesc()));
            } else {
                msg.fail(404, "not found");
            }
        }
    }

    private void handleUnregister(Message<SvcDesc> msg) {
        log.info("svc.unregister {}", msg.body());

        map.getOrDefault(msg.body().getName(), new HashSet<>())
                .removeIf(svcStatus -> svcStatus.getDesc().equals(msg.body()));

    }

    private void handleRegister(Message<SvcDesc> req) {
        log.info("receive msg {}", req.body().toString());

        final SvcStatus svc = new SvcStatus(req.body(), true);
        final String key = svc.getDesc().getName();
        final Set<SvcStatus> one = new TreeSet<>();
        one.add(svc);
        map.merge(key, one, (pre, cur) -> {
            pre.addAll(cur);
            return pre;
        });

        if (map.get(key).contains(svc)) {
            vertx.deployVerticle(new HealthCheckVerticle(svc))
                    .onSuccess(handler -> {
                        svc.setAlive(false);
                        svc.setHealthCheckVerticleId(handler);
                    });
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("stop the cache verticle");
    }
}