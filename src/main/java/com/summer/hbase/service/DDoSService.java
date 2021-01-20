package com.summer.hbase.service;


import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.dao.DDoSDao;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DDoSService {


    /**
     * 展示全部的表
     *
     * @return
     * @throws IOException
     */
    public List<String> listTable() throws IOException {

        return DDoSDao.getTableByNameSpace();

    }

    /**
     * 展示表中的所有应用ID
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public Set<String> listYXID(String tableName) throws IOException {

        return DDoSDao.getAllYXIDByTable(tableName);

    }


    public List<BoDdosScreenStatus> listDataByYXIDAndTm(String tableName, String yxid, double realTime, int step) throws IOException, InterruptedException {


        int int_time = judgeTime(new Double(realTime).intValue());

        List<BoDdosScreenStatus> dataByYXIDPrefix = DDoSDao.getDataByYXIDPrefix(tableName, yxid);

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


        DDoSDao.scanTable(Constants.DDOS_TABLE);
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