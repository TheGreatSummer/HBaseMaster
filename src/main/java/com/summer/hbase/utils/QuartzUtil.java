package com.summer.hbase.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.summer.hbase.bean.PlayBackSetting;
import com.summer.hbase.config.QuartzConfig;
import org.apache.log4j.spi.LoggerFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * author:summer
 * date:2021/1/18
 */
@Component
public class QuartzUtil {

    @Autowired
    private Scheduler scheduler;


    public void addJob2Start(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass,
                             PlayBackSetting setting) {

        System.out.println("hhh");
        System.out.println(setting.toString());
        try {

            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();

            SimpleTrigger trigger = null;

            if(setting.getFrequency() >= 9999){
                 trigger = triggerBuilder.withIdentity(triggerName, triggerGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(1)
                                .repeatForever())
                        .build();
            }else{
                trigger = triggerBuilder.withIdentity(triggerName, triggerGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(1)
                                .withRepeatCount(setting.getFrequency()))
                        .build();
            }


            trigger.getJobDataMap().put("setting",setting);

            scheduler.scheduleJob(jobDetail, trigger);

            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void modifyJobTime(String triggerName, String triggerGroupName, PlayBackSetting setting) {
        System.out.println("modify the step");
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            trigger.getJobDataMap().remove("setting");
            trigger.getJobDataMap().put("setting",setting);

            scheduler.rescheduleJob(triggerKey,trigger);
//            if (!oldTime.equalsIgnoreCase(cron)) {
//                /** 方式一 ：调用 rescheduleJob 开始 */
//                // 触发器
//                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
//                // 触发器名,触发器组
//                triggerBuilder.withIdentity(triggerName, triggerGroupName);
//                triggerBuilder.startNow();
//                // 触发器时间设定
//                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
//                // 创建Trigger对象
//                trigger = (CronTrigger) triggerBuilder.build();
//                // 方式一 ：修改一个任务的触发时间
//                scheduler.rescheduleJob(triggerKey, trigger);
                /** 方式一 ：调用 rescheduleJob 结束 */

                /** 方式二：先删除，然后在创建一个新的Job */
                // JobDetail jobDetail =
                // scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
                // Class<? extends Job> jobClass = jobDetail.getJobClass();
                // removeJob(jobName, jobGroupName, triggerName,
                // triggerGroupName);
                // addJob(jobName, jobGroupName, triggerName, triggerGroupName,
                // jobClass, cron);
                /** 方式二 ：先删除，然后在创建一个新的Job */
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @Description: 移除一个任务
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {

        System.out.println("remove "+jobName);

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void pauseJob(String jobName,String jobGroupName){
        System.out.println("pause "+jobName);
        try{
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.pauseJob(jobKey);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resumeJob(String jobName,String jobGroupName){

        System.out.println("resume "+jobName);
        try{
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.resumeJob(jobKey);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


}