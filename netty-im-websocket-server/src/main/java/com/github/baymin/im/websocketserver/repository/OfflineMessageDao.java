package com.github.baymin.im.websocketserver.repository;

import com.github.baymin.im.websocket.domain.MessageInfo;

import java.util.Set;

/**
 * 离线消息数据库交互接口
 *
 * @author Zongwei
 * @date 2020/4/17 14:52
 */
public interface OfflineMessageDao {

    /**
     * 新增一条离线消息
     *
     * @param userId      用户ID
     * @param messageInfo 消息信息
     */
    void insert(String userId, MessageInfo messageInfo);

    /**
     * 查询用户是否存在离线消息
     *
     * @param userId 用户ID
     * @return true:存在离线消息 false:不存在离线消息
     */
    boolean checkOfflineMessage(String userId);

    /**
     * 获取未发送的离线消息
     *
     * @param userId              用户ID
     * @param latestSendMessageId 最后成功发送的消息ID
     * @return Set<MessageInfo>
     */
    Set<MessageInfo> getOfflineMessage(String userId, Long latestSendMessageId);

    /**
     * 清除离线消息的flag
     *
     * @param userId 用户ID
     */
    void clearOfflineFlag(String userId);
}
