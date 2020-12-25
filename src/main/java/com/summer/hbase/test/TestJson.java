package com.summer.hbase.test;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoTdStatus;

public class TestJson {

    public static void main(String[] args) {

//        JSONArray json_array = JSONArray.parseArray("[{\"llid\":\"Llid\",\"mc\":\"mc\",\"st\":0,\"tp\":\"Tp\"},{\"llid\":\"Llid\",\"mc\":\"mc\",\"st\":1,\"tp\":\"Tp\"},{\"llid\":\"Llid\",\"mc\":\"mc\",\"st\":2,\"tp\":\"Tp\"}]");
//        int size = json_array.size();
//        for(int i=0;i<size;i++){
//            String text = (String) json_array.getJSONObject(i).toString();
//            System.out.println(text);
//            BoLinkStatus parse = JSONArray.parseObject(text,BoLinkStatus.class);
//            System.out.println(parse.toString());
//        }

        BoTdStatus jsonObject = JSONArray.parseObject("{\"maxtcp\":1,\"maxudp\":1,\"mintcp\":1,\"minudp\":1,\"tcp\":0,\"udp\":0}", BoTdStatus.class);
        System.out.println(jsonObject);

    }
}
