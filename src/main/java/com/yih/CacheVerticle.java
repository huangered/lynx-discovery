package com.yih;

import com.google.gson.Gson;
import com.yih.pojo.RegisterRequest;
import com.yih.pojo.RegisterSvc;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CacheVerticle extends AbstractVerticle {

    private Gson gson = new Gson();
    private Map<String, Set<RegisterSvc>> map = new ConcurrentHashMap<>();

    @Override
    public void start() throws Exception {
        EventBus bus = vertx.eventBus();
        bus.<JsonObject>consumer("svc.register", msg -> {
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
        });

        bus.<String>consumer("svc.query", msg -> {
            log.info("svc.query {}", msg.body());
            String svcName = msg.body();
            Set<RegisterSvc> ss = map.getOrDefault(svcName, new HashSet<>());
            if (ss.isEmpty()) {
                msg.reply("");
            } else {
                RegisterSvc svc = ss.stream().findAny().get();
                msg.reply(String.format("%s:%s", svc.getReq().getUrl(), svc.getReq().getPort()));
            }
        });

        vertx.setPeriodic(5000, print -> {
            log.info("Print map {}", map);
        });
    }

    @Override
    public void stop() throws Exception {
    }
}