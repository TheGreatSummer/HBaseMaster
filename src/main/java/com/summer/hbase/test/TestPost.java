package com.summer.hbase.test;


import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.*;
import com.summer.hbase.utils.SendUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestPost {
    public static  void main(String[] args) throws IOException {

        BoDdosScreenStatus screenStatus = new BoDdosScreenStatus();
        BoLinkStatus linkStatus = new BoLinkStatus();
        linkStatus.setLlid("Llid");
        linkStatus.setSt(new Byte("0"));
        linkStatus.setTp("Tp");
        linkStatus.setMc("mc");
        List<BoLinkStatus> linkList = new ArrayList<>();
        linkList.add(linkStatus);



        BoServerStatus serverStatus = new BoServerStatus();
        serverStatus.setCpu(1);
        serverStatus.setMem(1);
        serverStatus.setServerId("serverId");
        ArrayList<BoServerStatus> serverList = new ArrayList<>();
        serverList.add(serverStatus);

        BoTdStatus tdStatus = new BoTdStatus();
        tdStatus.setMaxtcp(1);
        tdStatus.setMaxudp(1);
        tdStatus.setMintcp(1);
        tdStatus.setMinudp(1);

        BoVictimStatus victimStatus = new BoVictimStatus();
        victimStatus.setCpu(1);
        victimStatus.setMc("mc");
        victimStatus.setMem(1);
        victimStatus.setRate(1);
        victimStatus.setSyn_recv(0);


        screenStatus.setLinks(linkList);
        screenStatus.setServers(serverList);
        screenStatus.setTd(tdStatus);
        screenStatus.setTm(0);
        screenStatus.setWlid("Wlid1");
        screenStatus.setVicSt(victimStatus);

        String s = JSON.toJSONString(screenStatus);
        System.out.println(s);

        SendUtil sendUtil = new SendUtil();

        for (int i = 0; i < 100; i++) {
            System.out.println(screenStatus.toString());

            sendUtil.sendToTsdb(screenStatus,"hdflsjhdlfjhsadljhsadljka");

            screenStatus.setTm(new Long(screenStatus.getTm()).intValue() + 5);
        }







        System.out.println("序列化已发送...");

    }



}
