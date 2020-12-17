package com.yih.lynx.server;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LynxOption {
    private int port;
    private boolean wait;
}