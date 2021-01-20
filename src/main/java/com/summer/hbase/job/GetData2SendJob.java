package com.summer.hbase.job;

import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.PlayBackSetting;
import com.summer.hbase.utils.QuartzUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * author:summer
 * date:2021/1/18
 */
public class GetData2SendJob implements Job {


    @Autowired
    private QuartzUtil quartzUtil;

    public void execute(JobExecutionContext jobExecutionContext) {
        PlayBackSetting setting = (PlayBackSetting)jobExecutionContext.getTrigger().getJobDataMap().get("setting");
//        System.out.println(setting.toString());

        String tableName = setting.getTableName();
        int step = setting.getStep();
        int realTime = setting.getRealTime();
        String yxid = setting.getYxid();

        String rowkey_pre = yxid+"_"+String.valueOf(setting.getRealTime());

        BoDdosScreenStatus dataByKeyPre = null;
//        try {
//            dataByKeyPre = HBaseDao.getDataByKeyPre(tableName, rowkey_pre);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(dataByKeyPre.toString());
        System.out.println("-----  content  -----");
        System.out.println(setting.toString());
        System.out.println(rowkey_pre);

        //update realTime by add step to create new rowkey_pre
        setting.setRealTime(setting.getRealTime()+step);

    }
}
