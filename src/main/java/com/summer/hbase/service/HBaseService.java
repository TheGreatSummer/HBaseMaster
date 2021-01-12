package com.summer.hbase.service;


import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.dao.HBaseDao;
import com.summer.hbase.utils.SendUtil;
import com.summer.hbase.websocket.WebSocketServer;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;
import sun.plugin.net.protocol.jar.CachedJarURLConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HBaseService {


    /**
     * 展示全部的表
     *
     * @return
     * @throws IOException
     */
    public List<String> listTable() throws IOException {

        return HBaseDao.getTableByNameSpace();

    }

    /**
     * 展示表中的所有应用ID
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public Set<String> listYXID(String tableName) throws IOException {

        return HBaseDao.getAllYXIDByTable(tableName);

    }


    public List<BoDdosScreenStatus> listDataByYXIDAndTm(String tableName, String yxid, double realTime, int step) throws IOException, InterruptedException {

        int int_time = judgeTime(new Double(realTime).intValue());

        List<BoDdosScreenStatus> dataByYXIDPrefix = HBaseDao.getDataByYXIDPrefix(tableName, yxid);

        List<BoDdosScreenStatus> send_list = new ArrayList<>();

        for (BoDdosScreenStatus ddos : dataByYXIDPrefix) {
            int tm_temp = (int) ddos.getTm();
            if (tm_temp == int_time) {

                send_list.add(ddos);
                int_time = int_time + step; //时间间隔 后期可能还需要修改
            }

        }

        return send_list;
    }

    public void scanAll() throws IOException {


        HBaseDao.scanTable("screen:ddos");
        System.out.println("完毕");

    }


    public int judgeTime(int time) {
        int a = time % 5;

        if (a == 0) {
            return time;
        } else if (a >= 3) {
            return time + 5 - a;
        } else {
            return time - a;
        }

    }

}