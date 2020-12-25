package com.summer.hbase.bean;

import java.io.Serializable;
import java.util.StringJoiner;

public class BoVictimStatus implements Serializable {

    private static final long serialVersionUID = 8498376270691942607L;

    private String mc;

    private double rate; //被攻击情况下，节点处理正常服务的成功率

    private double cpu; //cpu使用率

    private double mem; //内存使用率

    private int syn_recv;   //SYN FLOOD攻击下，SYN_RECV连接数

    //TODO 未来扩展更多监控指标


    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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

    public int getSyn_recv() {
        return syn_recv;
    }

    public void setSyn_recv(int syn_recv) {
        this.syn_recv = syn_recv;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoVictimStatus.class.getSimpleName() + "[", "]")
                .add("mc='" + mc + "'")
                .add("rate=" + rate)
                .add("cpu=" + cpu)
                .add("mem=" + mem)
                .add("syn_recv=" + syn_recv)
                .toString();
    }
}
