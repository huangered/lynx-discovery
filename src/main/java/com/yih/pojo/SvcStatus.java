package com.yih.pojo;

import lombok.Data;

import java.util.Objects;

@Data
public class SvcStatus {
    private SvcDesc desc;
    private boolean alive;
    private String healthCheckVerticleId;

    public SvcStatus(SvcDesc desc, boolean alive) {
        this.desc = desc;
        this.alive = alive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SvcStatus that = (SvcStatus) o;

        return Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return desc != null ? desc.hashCode() : 0;
    }
}
