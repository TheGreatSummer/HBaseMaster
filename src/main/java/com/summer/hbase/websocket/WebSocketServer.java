package com.summer.hbase.websocket;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoDdosScreenStatus;
import com.summer.hbase.controller.HBaseController;
import com.summer.hbase.service.HBaseService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

@ServerEndpoint("/tsdb/playback/{yxid}")
@Component
public class WebSocketServer {
    static Log log = LogFactory.get(WebSocketServer.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收的yxid
     */
    private String yxid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("yxid") String yxid) {
        this.session = session;
        this.yxid = yxid;
        if (webSocketMap.containsKey(yxid)) {
            webSocketMap.remove(yxid);
            webSocketMap.put(yxid, this);
            //加入set中
        } else {
            webSocketMap.put(yxid, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("攻击场景:" + yxid + "-->开始连接:" + getOnlineCount());

        try {
            sendMessage("攻击场景" + yxid + "连接成功");
        } catch (IOException e) {
            log.error("攻击场景:" + yxid + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(yxid)) {
            webSocketMap.remove(yxid);
            //从set中删除
            subOnlineCount();
        }
        log.info("攻击场景:" + yxid + "退出," + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("攻击场景:" + yxid + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文  参数与 controller中的参数相似
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.yxid);
                String toYXID = jsonObject.getString("yxid");
                String tableName = jsonObject.getString("tableName");
                int timestamp = Integer.parseInt(jsonObject.getString("time"));
                int length = Integer.parseInt(jsonObject.getString("length"));
                int speed = Integer.parseInt(jsonObject.getString("speed"));

                System.out.println("tableName:" + tableName + ",yxid:" + toYXID + ",time:" + timestamp + ",length:" + length + ",speed:" + speed);


                if (StringUtils.isNotBlank(toYXID) && webSocketMap.containsKey(toYXID)) {
                    System.out.println("开始回放的数据的抽取");
                    HBaseService hBaseService = new HBaseService();
                    HBaseController hBaseController = new HBaseController();

                    hBaseController.getDataToPlayback(tableName, toYXID, timestamp, speed);

                    webSocketMap.get(toYXID).sendMessage("send over");
                } else {
                    log.error("请求的yxid:" + toYXID + "不在该服务器上");

                }

                webSocketMap.get(toYXID).sendMessage("playback over");


            } catch (NullPointerException e) {
                System.out.println("下线了，不传输了");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("攻击场景错误:" + this.yxid + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("攻击场景" + userId + ",不在线！");
        }
    }

    /**
     * 自定义关闭
     */
    public static void Close(String yxid) {
        if (webSocketMap.containsKey(yxid)) {
            webSocketMap.remove(yxid);
            //从set中删除
            System.out.println(yxid);
            subOnlineCount();
        }
        log.info("场景退出:" + yxid + ",当前在线人数为:" + getOnlineCount());
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized boolean iscontain(String yxid) {

        return webSocketMap.containsKey(yxid);
    }
}