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

        Set<String> allKey = HBaseDao.getAllKeyByWlid(tableName, wlid);

        List<BoDdosScreenStatus> list_ddos = new ArrayList<>();
        for(String rowkey : allKey){
            BoDdosScreenStatus dataByKey = HBaseDao.getDataByKey(tableName, rowkey);
            list_ddos.add(dataByKey);
        }

        return list_ddos;
    }
}
