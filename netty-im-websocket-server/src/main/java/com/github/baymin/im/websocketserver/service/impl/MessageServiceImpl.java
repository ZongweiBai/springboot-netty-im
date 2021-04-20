package com.github.baymin.im.websocketserver.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.baymin.im.websocket.domain.MessageInfo;
import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocketserver.entity.GroupInfo;
import com.github.baymin.im.websocket.service.MessageService;
import com.github.baymin.im.websocket.util.Constant;
import com.github.baymin.im.websocket.util.MessageType;
import com.github.baymin.im.websocketserver.repository.OfflineMessageDao;
import com.github.baymin.im.websocketserver.repository.OnlineMessageDao;
import com.github.baymin.im.websocketserver.service.GroupService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 具体消息处理类
 *
 * @author Zongwei
 * @date 2019/11/15 18:39
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Group操作相关service
     */
    @Autowired
    private GroupService groupService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OfflineMessageDao offlineMessageDao;

    @Autowired
    private OnlineMessageDao onlineMessageDao;

    /**
     * 用户注册
     *
     * @param userId
     * @param ctx
     */
    @Override
    public void register(String userId, ChannelHandlerContext ctx) {
        Constant.onlineUserMap.put(userId, ctx);
        ResponseInfo responseInfo = new ResponseInfo().success();
        responseInfo.setMessageType(MessageType.REGISTER);
        sendMessage(ctx, responseInfo, null);
        log.info("userId为 {} 的用户登记到在线用户表，当前在线人数为：{}", userId, Constant.onlineUserMap.size());

        // 查询用户是否有未发送的离线消息，按照时间依次发送
        checkAndSendOfflineMessage(userId);
    }

    @Override
    public void handlerMessageSend(MessageInfo messageInfo) {
        switch (messageInfo.getMessageType()) {
            // 发送消息和文件给单个人
            case SINGLE_SENDING, FILE_MSG_SINGLE_SENDING -> this.sendOne(messageInfo);
            // 群发消息和文件
            case GROUP_SENDING, FILE_MSG_GROUP_SENDING -> this.sendGroup(messageInfo);
            default -> {}
        }
    }

    /**
     * 发送消息给指定用户
     */
    @Override
    public void sendOne(MessageInfo messageInfo) {
        String toUserId = messageInfo.getToUserId();
        ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(toUserId);
        if (Objects.isNull(toUserCtx)) {
            log.trace("用户{}没有在本机进行连接，插入离线消息", toUserId);
            offlineMessageDao.insert(messageInfo.getToUserId(), messageInfo);
        } else {
            ResponseInfo responseInfo = new ResponseInfo(messageInfo).success();
            this.sendMessage(toUserCtx, responseInfo, toUserId);
        }
    }

    /**
     * 群发消息
     */
    @Override
    public void sendGroup(MessageInfo messageInfo) {

        String fromUserId = messageInfo.getFromUserId();
        String toGroupId = messageInfo.getToGroupId();

        GroupInfo groupInfo = groupService.getByGroupId(toGroupId);
        if (Objects.isNull(groupInfo)) {
            // TODO 思考怎么通知到消息发送者
            log.error("群组ID：{}不存在，无法发送", toGroupId);
            return;
        }
        ResponseInfo responseInfo = new ResponseInfo(messageInfo).success();
        groupInfo.getMembers().forEach(member -> {
            ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(member.getUserId());
            if (member.getUserId().equals(fromUserId)) {
                return;
            }
            if (Objects.isNull(toUserCtx)) {
                log.trace("用户{}没有在本机进行连接，插入离线消息", member.getUserId());
                offlineMessageDao.insert(member.getUserId(), messageInfo);
                return;
            }
            this.sendMessage(toUserCtx, responseInfo, member.getUserId());
        });
    }

    /**
     * 客户端断开连接
     *
     * @param ctx
     */
    @Override
    public void connectionDisconnect(ChannelHandlerContext ctx) {
        Iterator<Map.Entry<String, ChannelHandlerContext>> iterator = Constant.onlineUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ChannelHandlerContext> entry = iterator.next();
            if (entry.getValue() == ctx) {
                log.info("正在移除握手实例...");
                Constant.webSocketHandShakerMap.remove(ctx.channel().id().asLongText());
                ctx.close();
                log.info(MessageFormat.format("已移除握手实例，当前握手实例总数为：{0}", Constant.webSocketHandShakerMap.size()));
                iterator.remove();
                log.info(MessageFormat.format("userId为 {0} 的用户已退出聊天，当前在线人数为：{1}", entry.getKey(), Constant.onlineUserMap.size()));
                break;
            }
        }
    }

    /**
     * 发送类型不支持
     *
     * @param ctx
     */
    @Override
    public void caughtException(String message, ChannelHandlerContext ctx) {
        ResponseInfo responseInfo = new ResponseInfo().error(message);
        sendMessage(ctx, responseInfo, null);
    }

    /**
     * 发送消息给指定channel
     *
     * @param ctx
     * @param responseInfo
     * @param toUserId
     */
    @Override
    public void sendMessage(ChannelHandlerContext ctx, ResponseInfo responseInfo, String toUserId) {
        // 直接放入channel所对应的EventLoop的执行队列，减少ctx.channel().writeAndFlush(object)导致的线程频繁切换
        ctx.channel().eventLoop().execute(() -> {
            try {
                responseInfo.setDeliveryAt(LocalDateTime.now());
                ctx.channel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(responseInfo)));
                if (!StringUtils.isEmpty(toUserId)) {
                    onlineMessageDao.insertSendInfo(toUserId, responseInfo);
                }
            } catch (JsonProcessingException e) {
                log.error("发送消息失败", e);
            }
        });
    }

    @Override
    public boolean publishChatMessage(MessageInfo messageInfo) {
        try {
            redisTemplate.convertAndSend(Constant.PUB_SUB_MESSAGE_KEY, messageInfo);
            return true;
        } catch (Exception e) {
            log.error("发布到Redis出现异常", e);
            return false;
        }
    }

    /**
     * 查询用户是否有离线消息，存在则发送给用户
     *
     * @param userId 用户ID
     */
    private void checkAndSendOfflineMessage(String userId) {
        boolean hasOfflineMessage = offlineMessageDao.checkOfflineMessage(userId);
        if (!hasOfflineMessage) {
            return;
        }
        long latestSendMessageId = onlineMessageDao.getSendInfo(userId);
        Set<MessageInfo> offlineMessages = offlineMessageDao.getOfflineMessage(userId, latestSendMessageId);
        offlineMessages.forEach(this::handlerMessageSend);
        offlineMessageDao.clearOfflineFlag(userId);
    }
}
