package com.github.baymin.im.websocketserver.repository;

import com.github.baymin.im.websocket.domain.ResponseInfo;

/**
 * 在线消息数据库交互接口
 *
 * @author Zongwei
 * @date 2020/4/17 15:15
 */
public interface OnlineMessageDao {

    /**
     * 记录发送记录
     */
    void insertSendInfo(String toUserId, ResponseInfo responseInfo);

    /**
     * 获取最后一次发送的messageId
     */
    long getSendInfo(String userId);
}
