package com.summer.hbase.dao;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.utils.ParseDataUtils;
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
public class DDoSDao {

    public static void addDDOSData(BoDdosScreenStatus boDdosScreenStatus, String yxid) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table ddosTable = connection.getTable(TableName.valueOf(Constants.DDOS_TABLE));

        long ts = System.currentTimeMillis();

        String tm = new String(String.valueOf(boDdosScreenStatus.getTm()));

        String rowkey = yxid + "_" + tm + "_" + ts;

        System.err.println(rowkey);

        Put ddosPut = new Put(Bytes.toBytes(rowkey));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_WLID), Bytes.toBytes(Constants.DDOS_TABLE_CF_WLID), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getWlid())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_TM), Bytes.toBytes(Constants.DDOS_TABLE_CF_TM), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getTm())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_LINKS), Bytes.toBytes(Constants.DDOS_TABLE_CF_LINKS), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getLinks())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_SERVERS), Bytes.toBytes(Constants.DDOS_TABLE_CF_SERVERS), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getServers())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_TD), Bytes.toBytes(Constants.DDOS_TABLE_CF_TD), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getTd())));

        ddosPut.addColumn(Bytes.toBytes(Constants.DDOS_TABLE_CF_VICST), Bytes.toBytes(Constants.DDOS_TABLE_CF_VICST), Bytes.toBytes(JSONArray.toJSONString(boDdosScreenStatus.getVicSt())));

        ddosTable.put(ddosPut);


        ddosTable.close();
        connection.close();

    }

    public static BoDdosScreenStatus getDataByKey(String tableName, String rwokey) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        Get get = new Get(Bytes.toBytes(rwokey));

        Result result = table.get(get);

//        System.out.println(result);
        BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2DDoSObject(result);

        return boDdosScreenStatus;

    }

    public static BoDdosScreenStatus getDataByKeyPre(String tableName, String rowkey_pre) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(rowkey_pre));
        Scan scan = new Scan();
        scan.setFilter(prefixFilter);
        scan.setReversed(true);

        ArrayList<BoDdosScreenStatus> res_list = new ArrayList<>();
        ResultScanner scanner = table.getScanner(scan);

        Result next = scanner.next();

//        System.out.println(result);
        BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2DDoSObject(next);
        System.out.println(boDdosScreenStatus);

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
     * 用于获取表中的运行ID                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ，返回所有的攻击场景名称
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static Set<String> getAllYXIDByTable(String tableName) throws IOException {
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        HashSet<String> wlid_sets = new HashSet<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res : scanner) {
            for (Cell cell : res.rawCells()) {
                String[] key = Bytes.toString(CellUtil.cloneRow(cell)).split("_");
                System.out.println("YXID-->" + key[0]);
                wlid_sets.add(key[0]);
            }
        }
        connection.close();
        return wlid_sets;
    }

    /**
     * 根据运行ID，即主键前缀，获取所有的信息
     *
     * @param tableName
     * @param YXID_prefix
     * @return
     * @throws IOException
     */
    public static List<BoDdosScreenStatus> getDataByYXIDPrefix(String tableName, String YXID_prefix) throws IOException {
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(YXID_prefix));
        Scan scan = new Scan();
        scan.setFilter(prefixFilter);
        scan.setReversed(true);

        ArrayList<BoDdosScreenStatus> res_list = new ArrayList<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res : scanner) {
            BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2DDoSObject(res);
            res_list.add(boDdosScreenStatus);
        }
        connection.close();
        return res_list;
    }

    public static Set<String> getAllKeyByYXIDPrefix(String tableName, String YXID_prefix) throws IOException {
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Table table = connection.getTable(TableName.valueOf(tableName));

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(YXID_prefix));
        Scan scan = new Scan();
        scan.setFilter(prefixFilter);

        HashSet<String> key_sets = new HashSet<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result res : scanner) {
            for (Cell cell : res.rawCells()) {
                key_sets.add(Bytes.toString(CellUtil.cloneRow(cell)));
            }
        }
        connection.close();
        return key_sets;
    }

    public static void deleteByWlid(String tableName, String... rows) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);
        Table table = connection.getTable(TableName.valueOf(tableName));

        List<Delete> deleteList = new ArrayList<Delete>();
        for (String row : rows) {
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
        scan.setReversed(true);

        ResultScanner scanner = table.getScanner(scan);

        for (Result res : scanner) {
            BoDdosScreenStatus boDdosScreenStatus = ParseDataUtils.Data2DDoSObject(res);

            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(boDdosScreenStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }


//            WriteData.writeContent("ddosdata.txt",boDdosScreenStatus.toString());
//            try {
//                TimeUnit.SECONDS.sleep(1);
//
//                SendUtil sendUtil = new SendUtil();
//
//                sendUtil.sendToWeb(boDdosScreenStatus);
//
//            } catch (InterruptedException ie){
//                ie.printStackTrace();
//            }
        }

    }

    ;


    public static Integer getMaxTM(String tableName, String rowkey_pre) {
        try {
            Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            QualifierFilter tmFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("Tm")));

            PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(rowkey_pre));
            scan.setFilter(prefixFilter);

            ResultScanner scanner = table.getScanner(scan);
            List<Integer> int_list = new ArrayList<>();
            for (Result res : scanner) {
                for (Cell cell : res.rawCells()) {
                    if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("tm")) {
                        int_list.add((Integer.valueOf(Bytes.toString(CellUtil.cloneValue(cell)))));

                    }

                }
            }

            System.out.println(int_list);
            Integer tm_max = returnMaxInteger(int_list);
            System.out.println(tm_max);


            return tm_max;
        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }


    }


    public static Integer returnMaxInteger(List<Integer> list) {
        int max = 0;

        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i)) {
                max = list.get(i);
            }
        }

        return max;
    }


}
