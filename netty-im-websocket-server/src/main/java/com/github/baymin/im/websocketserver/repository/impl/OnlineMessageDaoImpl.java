package com.github.baymin.im.websocketserver.repository.impl;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocketserver.repository.OnlineMessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 在线消息数据库交互层
 *
 * @author Zongwei
 * @date 2020/4/17 15:16
 */
@Repository
public class OnlineMessageDaoImpl implements OnlineMessageDao {

    /**
     * 记录用户已发送消息的最后一个messageId，Redis类型：hashMap
     */
    private static final String ONLINE_MESSAGE_RECORD = "nettyim:message:online:record";

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Override
    public void insertSendInfo(String toUserId, ResponseInfo responseInfo) {
        hashOperations.put(ONLINE_MESSAGE_RECORD, toUserId, responseInfo.getMessageId());
    }

    @Override
    public long getSendInfo(String userId) {
        Object messageId = hashOperations.get(ONLINE_MESSAGE_RECORD, userId);
        if (Objects.isNull(messageId)) {
            return 0;
        }
        return Long.parseLong(String.valueOf(messageId));
    }
}
