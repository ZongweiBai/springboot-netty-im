package com.github.baymin.im.websocket.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.baymin.im.websocket.util.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收到的消息信息
 *
 * @author zongwei
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageInfo extends MessageBase {

    /**
     * 消息类型
     */
    @JsonProperty("type")
    private MessageType messageType;

    /**
     * 发送人ID
     */
    private String fromUserId;

    /**
     * 接受人ID
     */
    private String toUserId;

    /**
     * 接受群组ID
     */
    private String toGroupId;

    /**
     * 消息内容
     */
    @JsonProperty("content")
    private String content;

    /**
     * 文件名
     */
    private String originalFilename;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件url
     */
    private String fileUrl;

    public MessageInfo(MessageInfo messageInfo) {
        this.setMessageId(messageInfo.getMessageId());
        this.setCreatedAt(messageInfo.getCreatedAt());
        this.setDeliveryAt(messageInfo.getDeliveryAt());
        this.messageType = messageInfo.getMessageType();
        this.fromUserId = messageInfo.getFromUserId();
        this.toUserId = messageInfo.getToUserId();
        this.toGroupId = messageInfo.getToGroupId();
        this.content = messageInfo.getContent();
        this.originalFilename = messageInfo.getOriginalFilename();
        this.fileSize = messageInfo.getFileSize();
        this.fileUrl = messageInfo.getFileUrl();
    }
}
