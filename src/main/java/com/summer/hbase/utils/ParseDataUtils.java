package com.summer.hbase.utils;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.*;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;


public class ParseDataUtils {

    public static BoDdosScreenStatus Data2DDoSObject(Result result){


        BoDdosScreenStatus boDdosScreenStatus = new BoDdosScreenStatus();

        for (Cell cell:result.rawCells()) {

            // 处理 wlid
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("wlid")) {
//                System.out.println("wlid -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                boDdosScreenStatus.setWlid(Bytes.toString(CellUtil.cloneValue(cell)));
            }

            // 处理 Tm
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("tm")) {
                System.out.println("tm -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                boDdosScreenStatus.setTm(Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell))));
            }

            // 处理 link list
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("links")) {
//                System.out.println("links -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                ArrayList<BoLinkStatus> boLink_list = new ArrayList<>();
                JSONArray links_json = JSONArray.parseArray(Bytes.toString(CellUtil.cloneValue(cell)));

                int size = links_json.size();
                for (int i = 0; i < size; i++) {
                    String text = (String) links_json.getJSONObject(i).toString();
                    BoLinkStatus bolink = JSONArray.parseObject(text, BoLinkStatus.class);
//                    System.out.println(bolink.toString());
                    boLink_list.add(bolink);
                }
                boDdosScreenStatus.setLinks(boLink_list);
            }

            // 处理 server list
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("servers")) {
//                System.out.println("server -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                ArrayList<BoServerStatus> boServer_list = new ArrayList<>();
                JSONArray links_json = JSONArray.parseArray(Bytes.toString(CellUtil.cloneValue(cell)));

                int size = links_json.size();
                for (int i = 0; i < size; i++) {
                    String text = (String) links_json.getJSONObject(i).toString();
                    BoServerStatus boServer = JSONArray.parseObject(text, BoServerStatus.class);
//                    System.out.println(boServer.toString());
                    boServer_list.add(boServer);
                }
                boDdosScreenStatus.setServers(boServer_list);
            }

            // 处理 Td
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("td")) {
//                System.out.println("td -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                BoTdStatus boTd = JSONArray.parseObject(Bytes.toString(CellUtil.cloneValue(cell)), BoTdStatus.class);
                boDdosScreenStatus.setTd(boTd);
            }

            // 处理 Td
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("vicSt")) {
//                System.out.println("vicst -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                BoVictimStatus boVit = JSONArray.parseObject(Bytes.toString(CellUtil.cloneValue(cell)), BoVictimStatus.class);
                boDdosScreenStatus.setVicSt(boVit);
            }

        }
        System.out.println("... done ...");
        return boDdosScreenStatus;
    }


    public static BoNetStatus Data2NetMonitorObject(Result result){


        BoNetStatus boNetStatus = new BoNetStatus();

        for (Cell cell:result.rawCells()) {

            // 处理 wlid
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("wlid")) {
//                System.out.println("wlid -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                boNetStatus.setWlid(Bytes.toString(CellUtil.cloneValue(cell)));
            }

            // 处理 Tm
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("tm")) {
                System.out.println("tm -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                boNetStatus.setTm(Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell))));
            }

            // 处理 link list
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("links")) {
//                System.out.println("links -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                ArrayList<BoLinkStatus> boLink_list = new ArrayList<>();
                JSONArray links_json = JSONArray.parseArray(Bytes.toString(CellUtil.cloneValue(cell)));

                int size = links_json.size();
                for (int i = 0; i < size; i++) {
                    String text = (String) links_json.getJSONObject(i).toString();
                    BoLinkStatus bolink = JSONArray.parseObject(text, BoLinkStatus.class);
//                    System.out.println(bolink.toString());
                    boLink_list.add(bolink);
                }
                boNetStatus.setLinks(boLink_list);
            }

            // 处理 errordevs list
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("errordevs")) {
//                System.out.println("errordevs -->"+Bytes.toString(CellUtil.cloneValue(cell)));
                List<String> boError_list = new ArrayList<>();
                JSONArray links_json = JSONArray.parseArray(Bytes.toString(CellUtil.cloneValue(cell)));

                int size = links_json.size();
                for (int i = 0; i < size; i++) {
                    String text = (String) links_json.getJSONObject(i).toString();

                    boError_list.add(text);
                }
                boNetStatus.setErrordevs(boError_list);
            }



        }
        System.out.println("... done ...");
        return boNetStatus;
    }
}
