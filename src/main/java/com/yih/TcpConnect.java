package com.yih;

import io.vertx.core.Vertx;

public class TcpConnect {
    public static void main(String[] argc) {
        Vertx vertx = Vertx.vertx();
        vertx.createNetServer().connectHandler(socket -> {
            socket.handler(buf -> {
                String a = buf.getString(0, buf.length() - 1);
                System.out.println(a);
                System.out.println(Thread.currentThread().getName());
                socket.write(buf);
                socket.close();
            });
        }).listen(3000);
    }
}