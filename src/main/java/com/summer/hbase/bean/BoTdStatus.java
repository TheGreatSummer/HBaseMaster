package com.summer.hbase.bean;

import java.io.Serializable;
import java.util.StringJoiner;


public class BoTdStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4058013916604805149L;

    private long udp;   //udp平均发送速率mbps

    private long maxudp;   //udp最大平均发送速率mbps

    private long minudp;   //udp最小发送速率mbps

    private long tcp;   //tcp平均发送速率mbps

    private long maxtcp;   //tcp最大发送速率mbps

    private long mintcp;   //tcp最小发送速率mbps

    public long getMaxudp() {
        return maxudp;
    }

    public void setMaxudp(long maxudp) {
        this.maxudp = maxudp;
    }

    public long getMinudp() {
        return minudp;
    }

    public void setMinudp(long minudp) {
        this.minudp = minudp;
    }

    public long getMaxtcp() {
        return maxtcp;
    }

    public void setMaxtcp(long maxtcp) {
        this.maxtcp = maxtcp;
    }

    public long getMintcp() {
        return mintcp;
    }

    public void setMintcp(long mintcp) {
        this.mintcp = mintcp;
    }


    public long getUdp() {
        return udp;
    }

    public void setUdp(long udp) {
        this.udp = udp;
    }

    public long getTcp() {
        return tcp;
    }

    public void setTcp(long tcp) {
        this.tcp = tcp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoTdStatus.class.getSimpleName() + "[", "]")
                .add("udp=" + udp)
                .add("maxudp=" + maxudp)
                .add("minudp=" + minudp)
                .add("tcp=" + tcp)
                .add("maxtcp=" + maxtcp)
                .add("mintcp=" + mintcp)
                .toString();
    }
}

