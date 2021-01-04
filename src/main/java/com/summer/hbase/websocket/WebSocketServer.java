package com.summer.hbase.websocket;

import com.alibaba.fastjson.JSONArray;
import com.summer.hbase.bean.BoDdosScreenStatus;
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

@ServerEndpoint("/tsdb/playback/{CJID}")
@Component
public class WebSocketServer {
    static Log log=LogFactory.get(WebSocketServer.class);
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收的CJID*/
    private String CJID="";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("CJID") String CJID) {
        this.session = session;
        this.CJID=CJID;
        if(webSocketMap.containsKey(CJID)){
            webSocketMap.remove(CJID);
            webSocketMap.put(CJID,this);
            //加入set中
        }else{
            webSocketMap.put(CJID,this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("攻击场景:"+CJID+"-->开始连接:" + getOnlineCount());

        try {
            sendMessage("攻击场景"+CJID+"连接成功");
        } catch (IOException e) {
            log.error("攻击场景:"+CJID+",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(CJID)){
            webSocketMap.remove(CJID);
            //从set中删除
            subOnlineCount();
        }
        log.info("攻击场景:"+CJID+"退出," + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * {tableName}/{CJID}/{time}/{length}/{speed}
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("攻击场景:"+CJID+",报文:"+message);
        //可以群发消息
        //消息保存到数据库、redis
        if(StringUtils.isNotBlank(message)){
            try {
                //解析发送的报文  参数与 controller中的参数相似
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId",this.CJID);
                String toCJID=jsonObject.getString("CJID");
                String tableName = jsonObject.getString("tableName");
                int timestamp = Integer.parseInt(jsonObject.getString("time"));
                int length = Integer.parseInt(jsonObject.getString("length"));
                int speed = Integer.parseInt(jsonObject.getString("speed"));

                System.out.println("tableName:"+tableName+",CJID:"+toCJID+",time:"+timestamp+",length:"+length+",speed:"+speed);

                int return_int = length;

                while(length == return_int){

                    if(StringUtils.isNotBlank(toCJID)&&webSocketMap.containsKey(toCJID)){
                        System.out.println("开始回放的数据的抽取");
                        HBaseService hBaseService = new HBaseService();
                        List<BoDdosScreenStatus> boDdosScreenStatuses = hBaseService.listDataByWlidAndTm(tableName, toCJID, timestamp, length, speed);

                        return_int = boDdosScreenStatuses.size();
                        System.out.println("new size:"+return_int);
                        String returnJson = JSONArray.toJSON(boDdosScreenStatuses).toString();
                        //传送给对应CJID用户的websocket

                        webSocketMap.get(toCJID).sendMessage(returnJson);
                    }else{
                        log.error("请求的CJId:"+toCJID+"不在该服务器上");
                        //否则不在这个服务器上，发送到mysql或者redis
                        break;
                    }

                }
                //传输完毕
                if(length != return_int){
                    webSocketMap.get(toCJID).sendMessage("playback over");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("攻击场景错误:"+this.CJID+",原因:"+error.getMessage());
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
     * */
    public static void sendInfo(String message,@PathParam("userId") String CJID) throws IOException {
        log.info("发送消息到:"+CJID+"，报文:"+message);
        if(StringUtils.isNotBlank(CJID)&&webSocketMap.containsKey(CJID)){
            webSocketMap.get(CJID).sendMessage(message);
        }else{
            log.error("攻击场景"+CJID+",不在线！");
        }
    }
    /**
     * 自定义关闭
     */
    public static void Close(String Id) {
        if(webSocketMap.containsKey(Id)){
            webSocketMap.remove(Id);
            //从set中删除
            System.out.println(Id);
            subOnlineCount();
        }
        log.info("场景退出:"+Id+",当前在线人数为:" + getOnlineCount());
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
}