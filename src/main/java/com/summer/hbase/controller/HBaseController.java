package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.dao.HBaseDao;
import com.summer.hbase.service.HBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController  //该方法默认使用JSON视图
@RequestMapping("/tsdb")
public class HBaseController {


    @Autowired
    public HBaseService hBaseService;

    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @PostMapping(value = "/ddosData/{flag}")
    public String putDDosData(@RequestBody BoDdosScreenStatus boDdosScreenStatus,@PathVariable("flag") String flag) throws IOException {

        System.out.println("flag-->"+flag);
        System.out.println("DDoS data->"+JSON.toJSONString(boDdosScreenStatus));

        if(!boDdosScreenStatus.equals(null) && flag != null){
            HBaseDao.addDDOSData(boDdosScreenStatus,flag);
        }

        return "ok";
    }

    @GetMapping("/table")
    public BoRestResObj getTable() throws IOException {

        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!hBaseService.listTable().equals(null)){
            res = hBaseService.listTable().toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return boRestResObj;
    }

    /**
     * 罗列该表中所有的网络应用
     * @param tableName
     * @return
     * @throws IOException
     */
    @GetMapping("/wlid/{tableName}")
    public BoRestResObj getWlidByTable(@PathVariable("tableName") String tableName) throws IOException {
        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!hBaseService.listWlid(tableName).equals(null)){
            res = hBaseService.listWlid(tableName).toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return  boRestResObj;
    }

    /**
     * 根据选定的网络ID 罗列全部的rowkey
     * 并根据rowkey 获取全部对应的数据
     * @param tablename
     * @return
     * @throws IOException
     */
    @GetMapping("/reddos/{tableName}/{rowkey}/{time}/{length}")
    public BoRestResObj getWlidByTablde(@PathVariable("tableName") String tablename,@PathVariable("rowkey") String rowkey,@PathVariable("time") long time,@PathVariable("length") int length) throws IOException {

        List<BoDdosScreenStatus> boDdosScreenStatuses = hBaseService.listAllRowkeyByWlid(tablename, rowkey);

        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!boDdosScreenStatuses.equals(null)){
            res = boDdosScreenStatuses.toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return boRestResObj;
    }


}
