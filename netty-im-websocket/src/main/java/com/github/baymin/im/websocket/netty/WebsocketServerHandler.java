package com.github.baymin.im.websocket.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.baymin.im.websocket.domain.MessageInfo;
import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocket.service.MessageService;
import com.github.baymin.im.websocket.util.Constant;
import com.github.baymin.im.websocket.util.IdGenerator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * websocket进行消息处理的handler
 *
 * @author Zongwei
 * @date 2019/11/15 17:09
 */
@Slf4j
@NoArgsConstructor
@Sharable
public class WebsocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MessageService messageService;

    private IdGenerator idGenerator;

    public WebsocketServerHandler(MessageService messageService, IdGenerator idGenerator) {
        this.messageService = messageService;
        this.idGenerator = idGenerator;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断evt是否是IdleStateEvent(用于触发用户事件，包含读空闲/写空闲/读写空闲)
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.debug("进入读空闲...");
                // 关闭无用channel，避免浪费资源
                messageService.connectionDisconnect(ctx);
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.debug("进入写空闲...");
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.debug("进入读写空闲...");
            }
        }
    }

    /**
     * 读取连接消息并对消息进行处理
     *
     * @param channelHandlerContext 处理上下文
     * @param webSocketFrame        WebSocket组件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {
        doHandler(channelHandlerContext, webSocketFrame);
    }

    private void doHandler(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handsShaker = Constant.webSocketHandShakerMap.get(ctx.channel().id().asLongText());
            if (null == handsShaker) {
                sendErrorMessage(ctx, "该用户已经离线或者不存在该连接");
            } else {
                handsShaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            }
            return;
        }
        // ping请求
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // pong请求
        if (frame instanceof PongWebSocketFrame) {
            ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            sendErrorMessage(ctx, "不支持二进制文件");
        }

        String request = ((TextWebSocketFrame) frame).text();
        log.info("收到服务器消息：[{}]", request);
        MessageInfo messageInfo = null;
        try {
            messageInfo = OBJECT_MAPPER.readValue(request, MessageInfo.class);
        } catch (Exception e) {
            log.error("服务器消息转换异常", e);
            sendErrorMessage(ctx, "JSON字符串转换出错！");
        }
        if (Objects.isNull(messageInfo)) {
            sendErrorMessage(ctx, "参数为空！");
            log.warn("参数为空");
            return;
        }

        switch (messageInfo.getMessageType()) {
            // 注册
            case REGISTER -> messageService.register(messageInfo.getFromUserId(), ctx);
            // 消息发送
            case SINGLE_SENDING, GROUP_SENDING, FILE_MSG_SINGLE_SENDING, FILE_MSG_GROUP_SENDING -> {
                messageInfo.setMessageId(idGenerator.nextId());
                messageInfo.setCreatedAt(LocalDateTime.now());
                boolean result = messageService.publishChatMessage(messageInfo);
                if (!result) {
                    messageService.caughtException("发送消息失败", ctx);
                }
            }
            // 不支持的类型
            default -> messageService.caughtException("暂不支持该类型的聊天", ctx);
        }
    }

    /**
     * 描述：客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        messageService.connectionDisconnect(ctx);
    }

    /**
     * 异常处理：关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private void sendErrorMessage(ChannelHandlerContext ctx, String errorMsg) {
        ResponseInfo responseInfo = new ResponseInfo().error(errorMsg);
        messageService.sendMessage(ctx, responseInfo, null);
    }

}
