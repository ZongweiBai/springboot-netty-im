package com.github.baymin.im.websocket.service;

import com.github.baymin.im.websocket.domain.MessageInfo;
import com.github.baymin.im.websocket.domain.ResponseInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息业务处理接口
 *
 * @author Zongwei
 * @date 2020/4/17 9:30
 */
public interface MessageService {

    /**
     * 用户注册
     *
     * @param userId 用户ID
     * @param ctx    ChannelHandlerContext
     */
    void register(String userId, ChannelHandlerContext ctx);

    /**
     * 处理消息的发送
     *
     * @param messageInfo 具体消息信息
     */
    void handlerMessageSend(MessageInfo messageInfo);

    /**
     * 发送消息给指定用户
     *
     * @param messageInfo 接收到的消息
     */
    void sendOne(MessageInfo messageInfo);

    /**
     * 群发消息
     *
     * @param messageInfo 接收到的消息
     */
    void sendGroup(MessageInfo messageInfo);

    /**
     * 客户端断开连接
     *
     * @param ctx ChannelHandlerContext
     */
    void connectionDisconnect(ChannelHandlerContext ctx);

    /**
     * 发送出现异常
     *
     * @param message 异常信息
     * @param ctx     ChannelHandlerContext
     */
    void caughtException(String message, ChannelHandlerContext ctx);

    /**
     * 发送消息给指定channel
     *
     * @param ctx          ChannelHandlerContext
     * @param responseInfo 响应消息
     * @param toUserId
     */
    void sendMessage(ChannelHandlerContext ctx, ResponseInfo responseInfo, String toUserId);

    /**
     * 发布聊天消息到Redis
     *
     * @param messageInfo 聊天消息
     * @return true or false
     */
    boolean publishChatMessage(MessageInfo messageInfo);

}
