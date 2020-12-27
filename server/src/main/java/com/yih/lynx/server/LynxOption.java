package com.yih.lynx.server;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LynxOption {
    private String url;
    private int port;
}