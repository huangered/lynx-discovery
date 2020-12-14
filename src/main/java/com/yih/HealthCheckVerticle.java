package com.yih;

import com.yih.pojo.SvcStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthCheckVerticle extends AbstractVerticle {

    public static final int DELAY = 5000;
    private final SvcStatus svc;
    private WebClient webClient;
    private int errCount;
    private long timerId;

    public HealthCheckVerticle(SvcStatus svc) {
        this.errCount = 0;
        this.svc = svc;
    }

    @Override
    public void start() throws Exception {
        final String svcName = svc.getDesc().getName();
        final String svcUrl = svc.getDesc().getUrl();
        final int svcPort = svc.getDesc().getPort();
        webClient = WebClient.create(vertx);
        timerId = vertx.setPeriodic(DELAY, r -> {
            webClient.get(svcPort, svcUrl, "/")
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .ssl(true)
                    .send(ar -> {
                        if (ar.succeeded()) {
                            errCount = 0;
                            svc.setAlive(true);
                            log.info("svc {} test {}:{} success", svcName, svcUrl, svcPort);
                        } else {
                            errCount += 1;
                            svc.setAlive(false);
                            if (errCount > 10) {
                                // remove from map
                                // undeployment self
                                log.info("svc {} lost, unregister", svcName);
                                JsonObject obj = new JsonObject()
                                        .put("svcName", svcName)
                                        .put("depId", this.deploymentID());

                                vertx.eventBus().publish("svc.unregister", obj);
                                vertx.undeploy(this.deploymentID());
                            }
                        }
                    });
        });
    }

    @Override
    public void stop() throws Exception {
        vertx.cancelTimer(timerId);
    }
}