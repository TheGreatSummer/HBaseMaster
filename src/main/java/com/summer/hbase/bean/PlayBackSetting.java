package com.summer.hbase.bean;

/**
 * author:summer
 * date:2021/1/18
 */
public class PlayBackSetting {

    public String tableName;

    public String yxid;

    public int realTime;

    public int step;

    public int frequency;

    public PlayBackSetting(String tableName, String yxid, int realTime, int step, int frequency) {
        this.tableName = tableName;
        this.yxid = yxid;
        this.realTime = realTime;
        this.step = step;
        this.frequency = frequency;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getYxid() {
        return yxid;
    }

    public void setYxid(String yxid) {
        this.yxid = yxid;
    }

    public int getRealTime() {
        return realTime;
    }

    public void setRealTime(int realTime) {
        this.realTime = realTime;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "PlayBackSetting{" +
                "tableName='" + tableName + '\'' +
                ", yxid='" + yxid + '\'' +
                ", realTime=" + realTime +
                ", step=" + step +
                ", frequency=" + frequency +
                '}';
    }
}
