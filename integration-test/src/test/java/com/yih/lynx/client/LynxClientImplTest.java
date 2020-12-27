package com.yih.lynx.client;

import com.yih.lynx.core.SvcDesc;
import com.yih.lynx.server.LynxOption;
import com.yih.lynx.server.codec.SvcDescCodec;
import com.yih.lynx.server.verticle.LynxServerVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Lynx client default impl test")
public class LynxClientImplTest {

    static LynxOption option = LynxOption.builder().port(3000).build();

    @BeforeAll
    public static void before() {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(SvcDesc.class, new SvcDescCodec());

        Future<String> f = vertx.deployVerticle(new LynxServerVerticle(option));
        // wait the server ready for test
        while (!f.isComplete()) {
        }
    }

    @AfterAll
    public static void after() {
        Vertx.vertx().close();
    }

    @DisplayName("echo test")
    @Test
    public void echo() {
        LynxClient client = new LynxClientImpl("http://localhost", 3000);
        assertEquals(true, client.echo());
    }

    @DisplayName("register test")
    @Test
    public void register() {
        LynxClient client = new LynxClientImpl("http://localhost", 3000);
        assertEquals(true, client.register("a", "www.baidu.com", 443, "/"));
    }
}