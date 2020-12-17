package com.yih.lynx.server;

import lombok.Getter;

import java.util.List;

@Getter
public class LynxConfig {
    private List<String> hosts;

    public boolean cluster() {
        return hosts.size() > 1;
    }
}
