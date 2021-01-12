package com.summer.hbase.dao;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.utils.ParseDataUtils;
import com.summer.hbase.utils.SendUtil;
import com.summer.hbase.utils.WriteData;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class NetMonitorDao {


    public static void addNetMonitorData(BoNetStatus boNetStatus, String yxid) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table ddosTable = connection.getTable(TableName.valueOf(Constants.NETMONITOR_TABLE));

        long ts = System.currentTimeMillis();

        String rowkey = yxid+"_"+ ts;

        System.err.println(rowkey);

        Put ddosPut = new Put(Bytes.toBytes(rowkey));

        ddosPut.addColumn(Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_WLID),Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_WLID),Bytes.toBytes(JSONArray.toJSONString(boNetStatus.getWlid())));

        ddosPut.addColumn(Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_TM),Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_TM),Bytes.toBytes(JSONArray.toJSONString(boNetStatus.getTm())));

        ddosPut.addColumn(Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_LINKS),Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_LINKS),Bytes.toBytes(JSONArray.toJSONString(boNetStatus.getLinks())));

        ddosPut.addColumn(Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_ERRORDEVS),Bytes.toBytes(Constants.NETMONITOR_TABLE_CF_ERRORDEVS),Bytes.toBytes(JSONArray.toJSONString(boNetStatus.getErrordevs())));


        ddosTable.put(ddosPut);


        ddosTable.close();
        connection.close();

    }

    public static BoNetStatus getDataByKey(String tableName, String rwokey) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        Get get = new Get(Bytes.toBytes(rwokey));

        Result result = table.get(get);

//        System.out.println(result);
        BoNetStatus boNetStatus = ParseDataUtils.Data2NetMonitorObject(result);

        return boNetStatus;

    }

    /**
     * 用于获取表中的运行ID                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ，返回所有的攻击场景名称
     * @param tableName
     * @return
     * @throws IOException
     */
    public static Set<String> getAllYXIDByTable(String tableName)throws IOException{
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        HashSet<String> wlid_sets = new HashSet<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res:scanner){
            for(Cell cell:res.rawCells()){
                String[] key = Bytes.toString(CellUtil.cloneRow(cell)).split("_");
                System.out.println("YXID-->"+key[0]);
                wlid_sets.add(key[0]);
            }
        }
        connection.close();
        return wlid_sets;
    }

    /**
     * 根据运行ID，即主键前缀，获取所有的信息
     * @param tableName
     * @param YXID_prefix
     * @return
     * @throws IOException
     */
    public static List<BoNetStatus> getDataByYXIDPrefix(String tableName,String YXID_prefix)throws IOException{
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(YXID_prefix));
        Scan scan = new Scan();
        scan.setFilter(prefixFilter);
        scan.setReversed(true);

        ArrayList<BoNetStatus> res_list = new ArrayList<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res:scanner){
            BoNetStatus boNetStatus = ParseDataUtils.Data2NetMonitorObject(res);
            res_list.add(boNetStatus);
        }
        connection.close();
        return res_list;
    }

    public static Set<String> getAllKeyByYXIDPrefix(String tableName,String YXID_prefix)throws IOException{
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(YXID_prefix));
        Scan scan = new Scan();
        scan.setFilter(prefixFilter);

        HashSet<String> key_sets = new HashSet<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res:scanner){
            for(Cell cell:res.rawCells()){
                key_sets.add(Bytes.toString(CellUtil.cloneRow(cell)));
            }
        }
        connection.close();
        return key_sets;
    }

    public static void deleteByWlid(String tableName,String... rows) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);
        Table table = connection.getTable(TableName.valueOf(tableName));

        List<Delete> deleteList = new ArrayList<Delete>();
        for(String row:rows){
            Delete delete = new Delete(Bytes.toBytes(row));
            deleteList.add(delete);
        }
        table.delete(deleteList);


        connection.close();

    }

    //  测试全体遍历的功能
    public static void scanTable(String tableName) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);
        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        ResultScanner scanner = table.getScanner(scan);

        for(Result res: scanner){
            BoNetStatus boNetStatus = ParseDataUtils.Data2NetMonitorObject(res);

            WriteData.writeContent("ddosdata.txt",boNetStatus.toString());
            try {
                TimeUnit.SECONDS.sleep(5);

                SendUtil sendUtil = new SendUtil();

                sendUtil.sendToWeb(boNetStatus);

            } catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }

    };
}
