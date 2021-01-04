package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.dao.HBaseDao;
import com.summer.hbase.service.HBaseService;
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


    /**
     * 监听网络靶场平台传输的数据
     * @param boDdosScreenStatus  网络大屏数据
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/ddosData")
    public String putDDosData(@RequestBody BoDdosScreenStatus boDdosScreenStatus) throws IOException {

        String CJID = "10086";
        System.out.println("CJID-->"+CJID);
        System.out.println("DDoS data->"+JSON.toJSONString(boDdosScreenStatus));

        if(!boDdosScreenStatus.equals(null) && CJID != null){
            HBaseDao.addDDOSData(boDdosScreenStatus,CJID);
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
     * @param tablename 表名
     * @param CJID    攻击场景唯一ID
     * @param time      时间点
     * @param length    数据长度
     * @return
     * @throws IOException
     */
    @GetMapping("/playback/{tableName}/{CJID}/{time}/{length}/{speed}")
    public BoRestResObj getWlidByTablde(HttpServletRequest request,@PathVariable("tableName") String tablename,@PathVariable("CJID") String CJID,@PathVariable("time") long time,@PathVariable("length") int length,@PathVariable("speed") int speed) throws IOException, InterruptedException {

        HttpSession session = request.getSession();
        session.setAttribute("plagback_controller",CJID);


        List<BoDdosScreenStatus> boDdosScreenStatuses = hBaseService.listDataByWlidAndTm(tablename, CJID,time,length,speed);

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

    @GetMapping("/listall")
    public BoRestResObj listAll() throws IOException {
        hBaseService.scanAll();

        BoRestResObj boRestResObj = new BoRestResObj(1,"ok",null);

        return  boRestResObj;
    }

    @GetMapping("/playback/stop/{CJID}")
    public BoRestResObj remove(@PathVariable("CJID") String CJID) throws IOException {


        WebSocketServer.Close(CJID);

        BoRestResObj boRestResObj = new BoRestResObj(1,"close connect success",null);
        return  boRestResObj;
    }



}
