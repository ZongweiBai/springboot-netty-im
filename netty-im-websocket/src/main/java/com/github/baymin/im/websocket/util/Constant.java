package com.github.baymin.im.websocket.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket全局变量
 *
 * @author Zongwei
 * @date 2019/11/15 17:15
 */
public class Constant {

    public static final int RCVBUF_ALLOCATOR_SIZE = 592048;

    /**
    *  获取可用的socket端口
    *  public static final int WEBSOCKET_PORT = SocketUtils.findAvailableTcpPort();
    */
    public static final int WEBSOCKET_PORT = 14500;

    public interface WebsocketServerConfig {
        // 连接读空闲时间120秒
        int READ_IDLE_TIME = 120;

        // 写空闲60秒
        int WRITE_IDLE_TIME = 60;

        // 心跳响应超时时间，需大于空闲时间 秒
        int PING_TIME_OUT = 70;

        // 最大协议包长度 10k
        int MAX_FRAME_LENGTH = 1024 * 10;

        // 最大文本长度
        int MAX_AGGREGATED_CONTENT_LENGTH = 65536;

    }

    /**
     * 用户认证的键，用来匹配http session中的对应userId
     */
    public static final String USER_TOKEN = "userId";

    /**
     * 用channelId为键，存放握手实例。用来响应CloseWebSocketFrame的请求
     */
    public static Map<String, WebSocketServerHandshaker> webSocketHandShakerMap = new ConcurrentHashMap<>();

    /**
     * 用userId为键，存放在线的客户端连接上下文
     */
    public static Map<String, ChannelHandlerContext> onlineUserMap = new ConcurrentHashMap<>();

    /**
     * Redis发布和订阅消息的Key
     */
    public static final String PUB_SUB_MESSAGE_KEY = "nettyim:message:chat";

}
