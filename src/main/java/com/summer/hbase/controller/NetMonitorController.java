package com.summer.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.bean.BoRestResObj;
import com.summer.hbase.bean.PlayBackSetting;
import com.summer.hbase.constants.Constants;
import com.summer.hbase.dao.NetMonitorDao;
import com.summer.hbase.job.GetData2SendJob;
import com.summer.hbase.service.NetMonitorService;
import com.summer.hbase.utils.QuartzUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/netmonitor")
public class NetMonitorController {

    @Autowired
    public NetMonitorService netMonitorService;

    @Autowired
    public QuartzUtil quartzUtil;


    @PostMapping(value = "/netmonitorData/{yxid}")
    public BoRestResObj putMonitorData(@RequestBody BoNetStatus boNetStatus, @PathVariable("yxid") String yxid) throws IOException {

        System.out.println("yxid-->" + yxid);
        System.out.println("netMonitor data->" + JSON.toJSONString(boNetStatus));

        if (!boNetStatus.equals(null) && yxid != null) {
            NetMonitorDao.addNetMonitorData(boNetStatus, yxid);
        }

        BoRestResObj boRestResObj = new BoRestResObj(1, "ok", "ok");

        return boRestResObj;
    }

    @GetMapping("/yxid/{tableName}")
    public BoRestResObj getWlidByTable(@PathVariable("tableName") String tableName) throws IOException {
        int optres = 0;
        String msg = "failed";
        String res = "failed";
        if (!netMonitorService.listYXID(tableName).equals(null)) {
            res = netMonitorService.listYXID(tableName).toString();
            msg = "ok";
            optres = 1;
        }

        BoRestResObj boRestResObj = new BoRestResObj(optres, msg, res);

        return boRestResObj;
    }


    @GetMapping("/playback/start/{yxid}/{realTime}/{step}")
    public BoRestResObj getDataToPlayback(@PathVariable("yxid") String yxid, @PathVariable("realTime") long realTime, @PathVariable("step") int step) throws IOException, InterruptedException {


        System.out.println("netMonitor playback start ...");
        int timestamp = new Long(realTime).intValue();
        int maxTM = 10000;
//        int maxTM = NetMonitorDao.getMaxTM(Constants.DDOS_TABLE, (yxid+"_"));
        Integer fre = (maxTM - timestamp) / step + 1;
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.NETMONITOR_TABLE, yxid, timestamp, step, fre);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.addJob2Start(yxid, Constants.NETMONITOR_GROUPNAME, yxid,Constants.NETMONITOR_GROUPNAME, GetData2SendJob.class, playBackSetting);
            }
        }).start();

        return new BoRestResObj(1, yxid + " play back success", null);
    }


    @GetMapping("/playback/stop/{yxid}")
    public BoRestResObj stopPlayBack(@PathVariable("yxid") String yxid) {

        quartzUtil.removeJob(yxid, Constants.NETMONITOR_GROUPNAME, yxid, Constants.NETMONITOR_GROUPNAME);

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/playback/pause/{yxid}/{realTime}")
    public BoRestResObj pausePlayBack(@PathVariable("yxid") String yxid, @PathVariable("realTime") long realTime) {

        /**
         * 传输数据否 即将步长设为0
         *
         * 与draw有着相同的问题
         */

        quartzUtil.pauseJob(yxid, Constants.NETMONITOR_GROUPNAME);

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }

    @GetMapping("/playback/resume/{yxid}")
    public BoRestResObj resumePlayBack(@PathVariable("yxid") String yxid) {

        quartzUtil.resumeJob(yxid, Constants.NETMONITOR_GROUPNAME);

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
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.NETMONITOR_TABLE, yxid, timestamp, 0, 9999);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.modifyJobTime(yxid, Constants.NETMONITOR_GROUPNAME, playBackSetting);
            }
        }).start();
        return new BoRestResObj(1, "draw " + yxid + " at " + realTime + " ok", "ok");
    }

    @GetMapping("/playback/changestep/{yxid}/{step}/{realTime}")
    public BoRestResObj changeStepPlayBack(@PathVariable("yxid") String yxid, @PathVariable("step") int step, @PathVariable("realTime") long realTime) {


        System.out.println("调整步长。。。");
        int timestamp = new Long(realTime).intValue();
        int maxTM = 10000;
//        Integer maxTM = HBaseDao.getMaxTM("screen:ddos", (yxid+"_"));
        Integer fre = (maxTM - timestamp) / step + 1;
        PlayBackSetting playBackSetting = new PlayBackSetting(Constants.NETMONITOR_TABLE, yxid, timestamp, step, fre);

        new Thread(new Runnable() {
            @Override
            public void run() {
                quartzUtil.modifyJobTime(yxid, Constants.NETMONITOR_GROUPNAME, playBackSetting);
            }
        }).start();

        return new BoRestResObj(1, "stop " + yxid + " ok", "ok");
    }


}
