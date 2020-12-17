package com.yih.lynx.client;

import com.yih.lynx.server.LynxOption;
import com.yih.lynx.server.LynxServer;
import org.junit.jupiter.api.*;

@DisplayName("Lynx client default impl test")
public class LynxClientImplTest {

    static LynxOption option = LynxOption.builder().port(3000).wait(true).build();

    static LynxServer server = new LynxServer(option);

    @BeforeAll
    public static void before() {
        server.start();
    }

    @AfterAll
    public static void after() {
        server.stop();
    }

    @DisplayName("echo test")
    @Test
    public void echo() {
        LynxClient client = new LynxClientImpl("http://localhost", 3000,
                "a", "www.baidu.com", 443, "/");
        Assertions.assertEquals(true, client.echo());
    }

    @DisplayName("register test")
    @Test
    public void register() {
        LynxClient client = new LynxClientImpl("http://localhost", 3000,
                "a", "www.baidu.com", 443, "/");
        Assertions.assertEquals(true, client.register());
    }
}