package com.github.baymin.im.websocket.core;

import com.github.baymin.im.websocket.domain.MessageInfo;
import com.github.baymin.im.websocket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis订阅监听
 *
 * @author Zongwei
 * @date 2020/4/17 9:05
 */
@Slf4j
public class RedisCustomListener implements MessageListener {

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        doHandler(message);
    }

    private void doHandler(Message message) {
        log.debug("Channel:{} 接收到订阅消息:{}", new String(message.getChannel()), new String(message.getBody()));
        try {
            MessageInfo messageInfo = (MessageInfo) redisTemplate.getValueSerializer().deserialize(message.getBody());
            messageService.handlerMessageSend(messageInfo);
        } catch (Exception e) {
            log.error("处理订阅消息失败，出现异常：{}", e.getMessage(), e);
        }
    }

}
