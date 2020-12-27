package com.yih.lynx.server;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LynxConfig {
    private List<LynxOption> hosts = new ArrayList<>();

    public boolean cluster() {
        return hosts.size() > 1;
    }
}
