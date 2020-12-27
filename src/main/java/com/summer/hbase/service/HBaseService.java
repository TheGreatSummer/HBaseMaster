package com.summer.hbase.service;


import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.dao.HBaseDao;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class HBaseService {


    /**
     * 展示全部的表
     * @return
     * @throws IOException
     */
    public List<String> listTable() throws IOException {

        return HBaseDao.getTableByNameSpace();

    }

    /**
     * 展示表中的所有应用ID
     * @param tableName
     * @return
     * @throws IOException
     */
    public Set<String> listWlid(String tableName) throws IOException {

        return HBaseDao.getAllWlidByTable(tableName);

    }


    public List<BoDdosScreenStatus> listDataByWlidAndTm(String tableName,String wlid,long time,int length) throws IOException {

        Set<String> allData = HBaseDao.getAllKeyByWlid(tableName, wlid);

        List<BoDdosScreenStatus> list_ddos = new ArrayList<>();
        for(int i=0;i<length;i++) {


            for (String rowkey : allData) {
                BoDdosScreenStatus dataByKey = HBaseDao.getDataByKey(tableName, rowkey);
                System.out.println("tm-->" + dataByKey.getTm());

                // 当数据长度不满足时该如何处理，以及 结尾如何标记

                if (dataByKey.getTm() == time) {
                    list_ddos.add(dataByKey);
                }
            }
            time = time + 5; //时间间隔 后期可能还需要修改

        }
        return list_ddos;
    }
}
