package com.yih.lynx.server.verticle;

import com.google.gson.Gson;
import com.yih.lynx.client.LynxClient;
import com.yih.lynx.client.LynxClientImpl;
import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.core.SvcStatus;
import com.yih.lynx.server.LynxOption;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PeerVerticle extends AbstractVerticle {

    private List<LynxOption> hosts;

    public PeerVerticle(List<LynxOption> hosts) {
        this.hosts = hosts;
    }

    @Override
    public void start() throws Exception {
        EventBus bus = vertx.eventBus();
        bus.consumer("svc.register", this::handleRegister);
        bus.consumer("svc.unregister", this::handleUnregister);
    }

    /**
     * 接受register消息，转发给peer
     *
     * @param msg
     */
    private void handleRegister(Message<SvcDesc> msg) {
        final SvcDesc body = msg.body();
        if (body.isFromPeer()) {
            return;
        }
        for (LynxOption peer : hosts) {
            vertx.executeBlocking(handler -> {
                LynxClient client = new LynxClientImpl(peer.getUrl(), peer.getPort());
                boolean result = client.register(body.getName(), body.getUrl(), body.getPort(), body.getHealthUrl());
                handler.complete(result);
            }).onComplete(result -> {
                log.info("send peer msg to {}:{} status {}", peer.getUrl(), peer.getPort(), result.result());
            });
        }
    }

    /**
     * 接受unregister，转发给peer
     *
     * @param msg
     */
    private void handleUnregister(Message<SvcDesc> msg) {
        final SvcDesc body = msg.body();
        if (body.isFromPeer()) {
            return;
        }
        for (LynxOption peer : hosts) {
            vertx.executeBlocking(handler -> {
                LynxClient client = new LynxClientImpl(peer.getUrl(), peer.getPort());
                boolean result = client.unregister(body.getName(), body.getUrl(), body.getPort(), body.getHealthUrl());
                handler.complete(result);
            }).onComplete(result -> {
                log.info("send peer msg to {}:{} status {}", peer.getUrl(), peer.getPort(), result.result());
            });
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("stop the peer verticle");
    }
}