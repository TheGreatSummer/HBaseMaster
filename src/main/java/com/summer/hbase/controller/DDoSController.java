package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.bean.PlayBackSetting;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.dao.DDoSDao;
import com.summer.hbase.job.GetData2SendJob;
import com.summer.hbase.service.DDoSService;
import com.summer.hbase.utils.QuartzUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController  //该方法默认使用JSON视图
@RequestMapping("/tsdb")
public class DDoSController {


    @Autowired
    public DDoSService hBaseService;

    @Autowired
    public QuartzUtil quartzUtil;

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("page")
    public ModelAndView page() {
        System.out.println("page");
        return new ModelAndView("demo");
    }


    @PostMapping(value = "/ddosData/{yxid}")
    public BoRestResObj putDDosData(@RequestBody BoDdosScreenStatus boDdosScreenStatus, @PathVariable("yxid") String yxid) throws IOException {

        System.out.println("yxid-->" + yxid);
        System.out.println("DDoS data->" + JSON.toJSONString(boDdosScreenStatus));

        if (!boDdosScreenStatus.equals(null) && yxid != null) {
            DDoSDao.addDDOSData(boDdosScreenStatus, yxid);
        }

        BoRestResObj boRestResObj = new BoRestResObj(1, "ok", "ok");

        return boRestResObj;
    }


    @GetMapping("/table")
    public BoRestResObj getTable() throws IOException {

        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if (!hBaseService.listTable().equals(null)) {
            res = hBaseService.listTable().toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres, msg, res);

        return boRestResObj;
    }

    /**
     * 罗列该表中所有的网络应用
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    @GetMapping("/yxid/{tableName}")
    public BoRestResObj getWlidByTable(@PathVariable("tableName") String tableName) throws IOException {
        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if (!hBaseService.listYXID(tableName).equals(null)) {
            res = hBaseService.listYXID(tableName).toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres, msg, res);

        return boRestResObj;
    }


    @GetMapping("/playback/start/{yxid}/{realTime}/{step}")
    public BoRestResObj getDataToPlayback(@PathVariable("yxid") String yxid, @PathVariable("realTime") long realTime, @PathVariable("step") int step) {

        System.out.println("回放开始。。。");
        int timestamp = new Long(realTime).intValue();
        int maxTM = 10000;
//        int maxTM = HBaseDao.getMaxTM(Constants.DDOS_TABLE, (yxid+"_"));
        Integer fre = (maxTM - timestamp) / step + 1;
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.DDOS_TABLE, yxid, timestamp, step, fre);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.addJob2Start(yxid, Constants.DDOS_GROUPNAME, yxid, Constants.DDOS_GROUPNAME, GetData2SendJob.class, playBackSetting);
            }
        }).start();

        return new BoRestResObj(1, yxid + " play back success", null);
    }


    @GetMapping("/playback/stop/{yxid}")
    public BoRestResObj stopPlayBack(@PathVariable("yxid") String yxid) {

        quartzUtil.removeJob(yxid, Constants.DDOS_GROUPNAME, yxid, Constants.DDOS_GROUPNAME);

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/playback/pause/{yxid}/{realTime}")
    public BoRestResObj pausePlayBack(@PathVariable("yxid") String yxid, @PathVariable("realTime") long realTime) {

        /**
         * 传输数据否 即将步长设为0
         */

        quartzUtil.pauseJob(yxid, Constants.DDOS_GROUPNAME);

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/playback/resume/{yxid}")
    public BoRestResObj resumePlayBack(@PathVariable("yxid") String yxid) {

        quartzUtil.resumeJob(yxid, Constants.DDOS_GROUPNAME);

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/playback/draw/{yxid}/{realTime}")
    public BoRestResObj drawPlayBack(@PathVariable("yxid") String yxid, @PathVariable("realTime") long realTime) {

        /**
         * 需求是一直回放当前的，那么如何才能接着开始呢，而且，前端程序似乎是有缓存的
         *
         * 一直时当当前的数据 那么step就是0,回放次数为永久放下去
         */
        System.out.println("draw ...");
        int timestamp = new Long(realTime).intValue();
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.DDOS_TABLE, yxid, timestamp, 0, 9999);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.modifyJobTime(yxid, Constants.DDOS_GROUPNAME, playBackSetting);
            }
        }).start();
        return new BoRestResObj(1, "draw " + yxid + " at " + realTime + " ok", "ok");
    }

    @GetMapping("/playback/changestep/{yxid}/{step}/{realTime}")
    public BoRestResObj changeStepPlayBack(@PathVariable("yxid") String yxid, @PathVariable("step") int step, @PathVariable("realTime") long realTime) {


        System.out.println("调整步长。。。");
        int timestamp = new Long(realTime).intValue();
        int maxTM = 10000;
//        int maxTM = HBaseDao.getMaxTM(Constants.DDOS_TABLE, (yxid+"_"));
        Integer fre = (maxTM - timestamp) / step + 1;
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.DDOS_TABLE, yxid, timestamp, step, fre);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.modifyJobTime(yxid, Constants.DDOS_GROUPNAME, playBackSetting);
            }
        }).start();

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/listall")
    public BoRestResObj listAll() throws IOException {
        hBaseService.scanAll();

        BoRestResObj boRestResObj = new BoRestResObj(1, "ok", null);

        return boRestResObj;
    }


}
