package com.summer.hbase.test;

import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.dao.HBaseDao;
import com.summer.hbase.utils.HBaseUtil;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
//        BoDdosScreenStatus hh = HBaseDao.getDataByKey("screen:ddos", "454dde98fab94057978d12490feeffcf_1608280152289");
//        System.out.println(hh.toString());

//        HBaseUtil.createTable("screen:netmonitor", Constants.DDOS_TABLE_VERSIONS,Constants.DDOS_TABLE_CF_LINKS,Constants.DDOS_TABLE_CF_SERVERS,
//                Constants.DDOS_TABLE_CF_TD,Constants.DDOS_TABLE_CF_TM,Constants.DDOS_TABLE_CF_VICST,Constants.DDOS_TABLE_CF_WLID);
//        HBaseUtil.crateNameSpace("screen");


        HBaseUtil.createTable(Constants.NETMONITOR_TABLE, Constants.NETMONITOR_TABLE_VERSIONS,Constants.NETMONITOR_TABLE_CF_LINKS,Constants.NETMONITOR_TABLE_CF_ERRORDEVS,Constants.NETMONITOR_TABLE_CF_TM,Constants.NETMONITOR_TABLE_CF_WLID);

    }
}
