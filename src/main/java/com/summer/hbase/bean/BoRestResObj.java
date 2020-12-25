/*
 * 向外部发送restful请求的返回结果对象
 */
package com.summer.hbase.bean;

import java.io.Serializable;
import java.util.StringJoiner;

public class BoRestResObj implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6006630824637842015L;

    private int optres;  //操作结果，0为失败，1为成功

    private String msg;  //若失败，则存储失败原因

    private String resObj; //返回对象的json字符串

    public BoRestResObj() {
        // TODO Auto-generated constructor stub
    }
    public BoRestResObj(int optres,String msg,String resObj){
        this.setOptres(optres);
        this.setMsg(msg);
        this.setResObj(resObj);
    }

    public int getOptres() {
        return optres;
    }

    public void setOptres(int optres) {
        this.optres = optres;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResObj() {
        return resObj;
    }

    public void setResObj(String resObj) {
        this.resObj = resObj;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoRestResObj.class.getSimpleName() + "[", "]")
                .add("optres=" + optres)
                .add("msg='" + msg + "'")
                .add("resObj='" + resObj + "'")
                .toString();
    }
}
