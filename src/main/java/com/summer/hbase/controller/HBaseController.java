package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.dao.HBaseDao;
import com.summer.hbase.service.HBaseService;
import com.summer.hbase.utils.SendUtil;
import com.summer.hbase.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @GetMapping("page")
    public ModelAndView page(){
        System.out.println("page");
        return new ModelAndView("demo");
    }


    @PostMapping(value = "/ddosData/{yxid}")
    public BoRestResObj putDDosData(@RequestBody BoDdosScreenStatus boDdosScreenStatus,@PathVariable("yxid") String yxid) throws IOException {


        System.out.println("yxid-->"+yxid);
        System.out.println("DDoS data->"+JSON.toJSONString(boDdosScreenStatus));

        if(!boDdosScreenStatus.equals(null) && yxid != null){
            HBaseDao.addDDOSData(boDdosScreenStatus,yxid);
        }

        int optres = 1;
        String msg = "ok";
        String res = "ok";


        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return  boRestResObj;

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
    @GetMapping("/yxid/{tableName}")
    public BoRestResObj getWlidByTable(@PathVariable("tableName") String tableName) throws IOException {
        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!hBaseService.listYXID(tableName).equals(null)){
            res = hBaseService.listYXID(tableName).toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return  boRestResObj;
    }


    @GetMapping("/playback/start/{tableName}/{yxid}/{realTime}/{step}")
    public BoRestResObj getDataToPlayback(@PathVariable("tableName") String tablename,@PathVariable("yxid") String yxid,@PathVariable("realTime") long realTime,@PathVariable("step") int step) throws IOException, InterruptedException {


        List<BoDdosScreenStatus> boDdosScreenStatuses = hBaseService.listDataByYXIDAndTm(tablename, yxid,realTime,step);

        SendUtil sendUtil = new SendUtil();
        sendUtil.sendDDOSListToWeb(boDdosScreenStatuses,yxid);

        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if(!boDdosScreenStatuses.equals(null)){
            res = "ok";
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres,msg,res);

        return boRestResObj;
    }

    @GetMapping("/listall")
    public BoRestResObj listAll() throws IOException {
        hBaseService.scanAll();

        BoRestResObj boRestResObj = new BoRestResObj(1,"ok",null);

        return  boRestResObj;
    }

    @GetMapping("/playback/stop/{yxid}")
    public BoRestResObj remove(@PathVariable("yxid") String yxid) throws IOException {


        WebSocketServer.Close(yxid);

        BoRestResObj boRestResObj = new BoRestResObj(1,yxid+"close connect success",null);
        return  boRestResObj;
    }



}
