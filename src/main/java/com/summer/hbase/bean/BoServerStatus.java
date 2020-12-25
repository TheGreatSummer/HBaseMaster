package com.summer.hbase.bean;

import java.io.Serializable;
import java.util.StringJoiner;

public class BoServerStatus implements Serializable {

    private static final long serialVersionUID = 5066964832178273480L;

    private String serverId;

    private double cpu; //cpu使用率

    private double mem; //内存使用率

    //TODO 未来扩展更多监控指标

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getMem() {
        return mem;
    }

    public void setMem(double mem) {
        this.mem = mem;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoServerStatus.class.getSimpleName() + "[", "]")
                .add("serverId=" + serverId)
                .add("cpu=" + cpu)
                .add("mem=" + mem)
                .toString();
    }
}
