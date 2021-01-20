package com.summer.hbase.utils;


import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.bean.BoNetStatus;
import com.summer.hbase.bean.BoRestResObj;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class SendUtil {


    public void sendToWeb(BoDdosScreenStatus boDdosScreenStatus){
        String URL = "http://192.168.31.183:8080/v2/netMonitorData";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoDdosScreenStatus> entity = new HttpEntity<>(boDdosScreenStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);

        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            System.out.println("POST DDosScreen Data to " + URL + " success");
        } else {
            System.out.println("POST failed:" + boRestResObj.getMsg());
        }

    }

    public void sendToWeb(BoNetStatus boNetStatus){
        String URL = "http://192.168.31.183:8080/v2/netMonitorData";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoNetStatus> entity = new HttpEntity<>(boNetStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);

        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            System.out.println("POST DDosScreen Data to " + URL + " success");
        } else {
            System.out.println("POST failed:" + boRestResObj.getMsg());
        }

    }

    /**
     * 使用Http POST请求，将网络大屏数据发送给时序数据库
     * @param boDdosScreenStatus 网络大屏数据
     * @param CJID      场景ID
     */
    public void sendToTsdb(BoDdosScreenStatus boDdosScreenStatus,String CJID){
        String URL = "http://localhost:8111/tsdb/ddosData/"+CJID;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoDdosScreenStatus> entity = new HttpEntity<>(boDdosScreenStatus, headers);


        RestTemplate restTemplate = new RestTemplate();
        String boRestResObj = restTemplate.postForObject(URL, entity, String.class);
        System.err.println(boRestResObj);

        assert boRestResObj != null;
        if (boRestResObj == "ok") {
            System.out.println("POST DDosScreen Data to " + URL + " success");
        } else {
            System.out.println("POST failed");
        }

    }
}