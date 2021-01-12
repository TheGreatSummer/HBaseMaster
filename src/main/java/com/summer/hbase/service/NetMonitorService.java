package com.summer.hbase.service;


import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.dao.NetMonitorDao;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NetMonitorService {


    /**
     * 展示表中的所有应用ID
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public Set<String> listYXID(String tableName) throws IOException {

        return NetMonitorDao.getAllYXIDByTable(tableName);

    }


    public List<BoNetStatus> listDataByYXIDAndTm(String tableName, String yxid, double realTime, int step) throws IOException, InterruptedException {

        int int_time = judgeTime(new Double(realTime).intValue());

        List<BoNetStatus> dataByYXIDPrefix = NetMonitorDao.getDataByYXIDPrefix(tableName, yxid);

        List<BoNetStatus> send_list = new ArrayList<>();

        for (BoNetStatus monitor : dataByYXIDPrefix) {
            int tm_temp = (int) monitor.getTm();
            if (tm_temp == int_time) {

                send_list.add(monitor);
                int_time = int_time + step; //时间间隔 后期可能还需要修改
            }

        }

        return send_list;
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