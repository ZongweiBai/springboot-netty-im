package com.github.baymin.im.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message的基础信息
 *
 * @author Zongwei
 * @date 2020/4/17 14:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageBase {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 消息创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 消息从服务器发出时间
     */
    private LocalDateTime deliveryAt;
}
