package com.summer.hbase.constants;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class Constants {

    public static Configuration CONFIGURATION = HBaseConfiguration.create();
//    static {
//        CONFIGURATION = HBaseConfiguration.create();
//        CONFIGURATION.set("hbase.zookeeper.quorum","myhbase");
//        CONFIGURATION.set("hbase.zookeeper.property.clientPort","2181");
//    }

    public static String NAMESPACE = "screen";

    //ddos攻击表
    public static  String DDOS_TABLE = "screen:ddos";
    public static String DDOS_TABLE_CF_WLID = "wlid";
    public static String DDOS_TABLE_CF_TM = "tm";
    public static String DDOS_TABLE_CF_SERVERS = "servers";
    public static String DDOS_TABLE_CF_VICST = "vicSt";
    public static String DDOS_TABLE_CF_TD = "td";
    public static String DDOS_TABLE_CF_LINKS = "links";

    public static int DDOS_TABLE_VERSIONS = 1;


    // 待添加
}
