package com.yih.lynx.server.verticle;

import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.server.LynxOption;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PeerVerticle extends AbstractVerticle {

    private List<LynxOption> hosts;
    private WebClient webClient;

    public PeerVerticle(List<LynxOption> hosts) {
        this.hosts = hosts;
    }

    @Override
    public void start() throws Exception {
        webClient = WebClient.create(vertx);
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
        body.setFromPeer(true);
        for (LynxOption peer : hosts) {
            webClient.post(peer.getPort(), peer.getUrl(), "/register")
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .sendJson(body, handler -> {
                        if (handler.failed()) {
                            log.error("register peer {}:{} fail", peer.getUrl(), peer.getPort());
                        }
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
        body.setFromPeer(true);
        for (LynxOption peer : hosts) {
            webClient.post(peer.getPort(), peer.getUrl(), "/unregister")
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .sendJson(body, handler -> {
                        if (handler.failed()) {
                            log.error("unregister peer {}:{} fail", peer.getUrl(), peer.getPort());
                        }
                    });
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("stop the peer verticle");
    }
}