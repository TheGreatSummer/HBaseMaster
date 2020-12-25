package com.summer.hbase.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class BoDdosScreenStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2827518726281241031L;

    private String wlid;    //网络id

    private long tm;    //时序间隔

    private List<BoServerStatus> servers = new ArrayList<>();   //集群中各服务器的资源监控数据

    private BoVictimStatus vicSt;   //被攻击节点的监控数据

    private BoTdStatus td;   //傀儡机ddos报文发送速率统计信息

    private List<BoLinkStatus> links = new ArrayList<>();   //链路流量吞吐量 topN



    public String getWlid() {
        return wlid;
    }

    public void setWlid(String wlid) {
        this.wlid = wlid;
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public List<BoServerStatus> getServers() {
        return servers;
    }

    public void setServers(List<BoServerStatus> servers) {
        this.servers = servers;
    }

    public BoVictimStatus getVicSt() {
        return vicSt;
    }

    public void setVicSt(BoVictimStatus vicSt) {
        this.vicSt = vicSt;
    }

    public void addServer(BoServerStatus server) {
        servers.add(server);
    }

    public void removeServer(BoServerStatus server) {
        servers.remove(server);
    }

    public BoTdStatus getTd() {
        return td;
    }

    public void setTd(BoTdStatus td) {
        this.td = td;
    }

    public List<BoLinkStatus> getLinks() {
        return links;
    }

    public void setLinks(List<BoLinkStatus> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoDdosScreenStatus.class.getSimpleName() + "[", "]")
                .add("wlid='" + wlid + "'")
                .add("tm=" + tm)
                .add("servers=" + servers)
                .add("vicSt=" + vicSt)
                .add("td=" + td)
                .add("links=" + links)
                .toString();
    }
}
