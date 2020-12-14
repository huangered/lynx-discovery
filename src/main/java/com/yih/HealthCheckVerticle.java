package com.yih;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthCheckVerticle extends AbstractVerticle {

    private final String svcName;
    private final String svcUrl;
    private final int svcPort;
    private WebClient webClient;
    private int errCount;
    private long timerId;

    public HealthCheckVerticle(String svcName, String svcUrl, int svcPort) {
        errCount = 0;
        this.svcName = svcName;
        this.svcUrl = svcUrl;
        this.svcPort = svcPort;
    }

    @Override
    public void start() throws Exception {
        webClient = WebClient.create(vertx);
        timerId = vertx.setPeriodic(5000, r -> {
            webClient.get(svcPort, svcUrl, "/")
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .ssl(true)
                    .send(ar -> {
                        if (ar.succeeded()) {
                            errCount = 0;
                            log.info("svc {} test {}:{} success", svcName, svcUrl, svcPort);
                        } else {
                            errCount += 1;
                            if (errCount > 10) {
                                // remove from map
                                // undeployment self
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