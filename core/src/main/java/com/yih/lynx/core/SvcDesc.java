package com.yih.lynx.core;

import lombok.Data;

import java.util.Objects;

@Data
public class SvcDesc {
    String name;
    String url;
    int port;
    String healthUrl;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", port=" + port +
                ", healthUrl='" + healthUrl + '\'' +
                '}';
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