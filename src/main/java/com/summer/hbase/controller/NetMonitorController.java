package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.dao.NetMonitorDao;
import com.summer.hbase.service.NetMonitorService;
import com.summer.hbase.utils.SendUtil;
import com.summer.hbase.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/netmonitor")
public class NetMonitorController {

    @Autowired
    public NetMonitorService netMonitorService;

//    @GetMapping("page")
//    public ModelAndView page(){
//        System.out.println("page");
//        return new ModelAndView("demo");
//    }


    @PostMapping(value = "/netmonitorData/{yxid}")
    public BoRestResObj putMonitorData(@RequestBody BoNetStatus boNetStatus, @PathVariable("yxid") String yxid) throws IOException {

        System.out.println("yxid-->"+yxid);
        System.out.println("netMonitor data->"+ JSON.toJSONString(boNetStatus));

        if(!boNetStatus.equals(null) && yxid != null){
            NetMonitorDao.addNetMonitorData(boNetStatus,yxid);
        }

        int optres = 1;
        String msg = "ok";
        String res = "ok";


        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return  boRestResObj;
    }

    /**
     * 罗列该表中所有的网络应用
     * @param tableName
     * @return
     * @throws IOException
     */
    @GetMapping("/yxid/{tableName}")
    public BoRestResObj getWlidByTable(@PathVariable("tableName") String tableName) throws IOException {
        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!netMonitorService.listYXID(tableName).equals(null)){
            res = netMonitorService.listYXID(tableName).toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return  boRestResObj;
    }


    @GetMapping("/playback/start/{tableName}/{yxid}/{realTime}/{step}")
    public BoRestResObj getDataToPlayback(@PathVariable("tableName") String tablename,@PathVariable("yxid") String yxid,@PathVariable("realTime") long realTime,@PathVariable("step") int step) throws IOException, InterruptedException {


        List<BoNetStatus> boNetStatusList = netMonitorService.listDataByYXIDAndTm(tablename, yxid,realTime,step);

        SendUtil sendUtil = new SendUtil();
        sendUtil.sendNetMonitorListToWeb(boNetStatusList,yxid);

        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!boNetStatusList.equals(null)){
            res = "ok";
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return boRestResObj;
    }



    @GetMapping("/playback/stop/{yxid}")
    public BoRestResObj remove(@PathVariable("yxid") String yxid) throws IOException {


        WebSocketServer.Close(yxid);

        BoRestResObj boRestResObj = new BoRestResObj(1,yxid+" close connect success",null);
        return  boRestResObj;
    }
}
