package com.summer.hbase.dao;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.utils.ParseDataUtils;
import com.summer.hbase.utils.SendUtil;
import com.summer.hbase.utils.WriteData;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

// 数据库连接部分可做统一规划
@Service
public class HBaseDao {

    public static void addDDOSData(BoDdosScreenStatus boDdosScreenStatus,String flag) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table ddosTable = connection.getTable(TableName.valueOf(Constants.DDOS_TABLE));

        long ts = System.currentTimeMillis();

        String rowkey = boDdosScreenStatus.getWlid() +flag+"_"+ ts;

        System.err.println(rowkey);

        Put ddosPut = new Put(Bytes.toBytes(rowkey));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_WLID),Bytes.toBytes(Constants.DDOS_TABLE_CF_WLID),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getWlid())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_TM),Bytes.toBytes(Constants.DDOS_TABLE_CF_TM),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getTm())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_LINKS),Bytes.toBytes(Constants.DDOS_TABLE_CF_LINKS),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getLinks())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_SERVERS),Bytes.toBytes(Constants.DDOS_TABLE_CF_SERVERS),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getServers())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_TD),Bytes.toBytes(Constants.DDOS_TABLE_CF_TD),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getTd())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_VICST),Bytes.toBytes(Constants.DDOS_TABLE_CF_VICST),Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getVicSt())));

        ddosTable.put(ddosPut);


        ddosTable.close();
        connection.close();

    }

    public static BoDdosScreenStatus getDataByKey(String tableName,String rwokey) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        Get get = new Get(Bytes.toBytes(rwokey));

        Result result = table.get(get);

//        System.out.println(result);
        BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2Object(result);

        return boDdosScreenStatus;

    }

    public static List<String> getTableByNameSpace() throws IOException {
        // 可添加对namespace的判断
        Connection conn = ConnectionFactory.createConnection(Constants.CONFIGURATION);
        Admin admin = conn.getAdmin();
        List<String> tablename_list = new ArrayList<String>();
        TableName[] screens = admin.listTableNamesByNamespace("screen");
        if (screens.length <= 0) {
            tablename_list.add("there is nothing");
            return tablename_list;
        }
        for (TableName tb : screens) {
            tablename_list.add(Bytes.toString(tb.getName()));
        }

        admin.close();
        conn.close();
        return tablename_list;
    }
    /**
     * 用于获取表中的所有攻击场景，返回所有的攻击场景名称
     * @param tableName
     * @return
     * @throws IOException
     */
    public static Set<String> getAllWlidByTable(String tableName)throws IOException{
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        HashSet<String> wlid_sets = new HashSet<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res:scanner){
            for(Cell cell:res.rawCells()){
                String[] key = Bytes.toString(CellUtil.cloneRow(cell)).split("flag");

                wlid_sets.add(key[0]);
            }
        }
        connection.close();
        return wlid_sets;
    }

    public static Set<String> getAllKeyByWlid(String tableName,String wlid_pattern)throws IOException{
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(wlid_pattern));
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
            BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2Object(res);

            WriteData.writeContent("ddosdata.txt",boDdosScreenStatus.toString());
//            try {
//                TimeUnit.SECONDS.sleep(1);
//
//                SendUtil sendUtil = new SendUtil();
//
//                sendUtil.sendToWeb(boDdosScreenStatus);
//
//            } catch (InterruptedException ie){}
        }

    };



}
