package com.yih.lynx.client;

import com.yih.lynx.core.SvcDesc;

public interface LynxClient {
    boolean echo();

    boolean register();

    boolean unregister();

    SvcDesc query(String name);
}