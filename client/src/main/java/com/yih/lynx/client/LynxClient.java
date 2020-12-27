package com.yih.lynx.client;

import com.yih.lynx.core.SvcDesc;

public interface LynxClient {
    boolean echo();

    boolean register(String svcName, String svcUrl, int svcPort, String svcHealthUrl);

    boolean unregister(String svcName, String svcUrl, int svcPort, String svcHealthUrl);

    SvcDesc query(String name);
}