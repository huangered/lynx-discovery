package com.yih.pojo;

public class RegisterRequest {
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
}