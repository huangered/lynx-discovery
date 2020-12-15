package com.yih.lynx.core;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SvcDesc {
    private String name;
    private String url;
    private int port;
    private String healthUrl;

    public SvcDesc(String svcName, String svcUrl, int svcPort, String svcHealthUrl) {
        this.name = svcName;
        this.url = svcUrl;
        this.port = svcPort;
        this.healthUrl = svcHealthUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SvcDesc that = (SvcDesc) o;

        if (port != that.port) return false;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}