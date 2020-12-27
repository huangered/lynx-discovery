package com.yih.lynx.core;

import lombok.Data;
import lombok.Getter;

import java.util.StringJoiner;

@Data
public class SvcDesc implements Comparable<SvcDesc> {
    private String name;
    private String url;
    private int port;
    private String healthUrl;
    private boolean ssl;

    /**
     * the message is from another lynx client for sync
     */
    private boolean fromPeer;

    public SvcDesc(String svcName, String svcUrl, int svcPort, String svcHealthUrl) {
        this.name = svcName;
        this.url = svcUrl;
        this.port = svcPort;
        this.healthUrl = svcHealthUrl;
        this.ssl = true;
    }

    public SvcDesc(SvcDesc svcDesc) {
        this.name = svcDesc.name;
        this.url = svcDesc.url;
        this.port = svcDesc.port;
        this.healthUrl = svcDesc.healthUrl;
        this.ssl = svcDesc.ssl;
        this.fromPeer = svcDesc.fromPeer;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", SvcDesc.class.getSimpleName() + " {", "}")
                .add("name='" + name + "'")
                .add("url='" + url + "'")
                .add("port=" + port)
                .add("healthUrl='" + healthUrl + "'")
                .add("ssl=" + ssl)
                .toString();
    }
}