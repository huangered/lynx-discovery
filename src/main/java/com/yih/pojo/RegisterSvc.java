package com.yih.pojo;

import lombok.Data;

import java.util.Objects;

@Data
public class RegisterSvc {
    private RegisterRequest req;
    private boolean alive;
    private String healthCheckVerticleId;

    public RegisterSvc(RegisterRequest req, boolean alive) {
        this.req = req;
        this.alive = alive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterSvc that = (RegisterSvc) o;

        return Objects.equals(req, that.req);
    }

    @Override
    public int hashCode() {
        return req != null ? req.hashCode() : 0;
    }
}
