package com.yih.lynx.core;

import lombok.Data;

@Data
public class SvcStatus implements Comparable<SvcStatus> {
    private SvcDesc desc;
    private boolean alive;
    private String healthCheckVerticleId;

    public SvcStatus(SvcDesc desc, boolean alive) {
        this.desc = desc;
        this.alive = alive;
    }

    @Override
    public int compareTo(SvcStatus svcStatus) {
        return desc.compareTo(svcStatus.desc);
    }
}
