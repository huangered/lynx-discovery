package com.yih.lynx.core;

import lombok.Getter;

@Getter
public class SvcDesc implements Comparable<SvcDesc> {
    private String name;
    private String url;
    private int port;
    private String healthUrl;
    private boolean ssl;


    public SvcDesc(String svcName, String svcUrl, int svcPort, String svcHealthUrl) {
        this.name = svcName;
        this.url = svcUrl;
        this.port = svcPort;
        this.healthUrl = svcHealthUrl;
        this.ssl = true;
    }

    @Override
    public int compareTo(SvcDesc svcDesc) {
        int r;
        if ((r = name.compareTo(svcDesc.name)) != 0) {
            return r;
        }
        if ((r = url.compareTo(svcDesc.url)) != 0) {
            return r;
        }
        return Integer.compare(port, svcDesc.port);
    }
}