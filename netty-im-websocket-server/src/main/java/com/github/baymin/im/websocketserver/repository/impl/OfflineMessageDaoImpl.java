package com.github.baymin.im.websocketserver.repository.impl;

import com.github.baymin.im.websocket.domain.MessageInfo;
import com.github.baymin.im.websocketserver.repository.OfflineMessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 离线消息数据库交互层
 *
 * @author Zongwei
 * @date 2020/4/17 14:52
 */
@Repository
public class OfflineMessageDaoImpl implements OfflineMessageDao {

    /**
     * 记录用户的离线消息的key，Redis类型：sortedSet
     */
    private static final String OFFLINE_MESSAGE_KEY = "nettyim:message:offline:%s";

    /**
     * 记录用户是否存在离线消息的key，Redis类型：hashMap
     */
    private static final String OFFLINE_MESSAGE_FLAG = "nettyim:message:offline:flag";

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    @Override
    public void insert(String userId, MessageInfo messageInfo) {
        zSetOperations.add(String.format(OFFLINE_MESSAGE_KEY, userId), messageInfo, messageInfo.getMessageId());
        hashOperations.put(OFFLINE_MESSAGE_FLAG, userId, true);
    }

    @Override
    public void clearOfflineFlag(String userId) {
        hashOperations.put(OFFLINE_MESSAGE_FLAG, userId, false);
    }

    @Override
    public boolean checkOfflineMessage(String userId) {
        Object offlineFlag = hashOperations.get(OFFLINE_MESSAGE_FLAG, userId);
        if (Objects.isNull(offlineFlag)) {
            return false;
        }
        return offlineFlag.equals(true);
    }

    @Override
    public Set<MessageInfo> getOfflineMessage(String userId, Long latestSendMessageId) {
        String redisKey = String.format(OFFLINE_MESSAGE_KEY, userId);
        zSetOperations.removeRangeByScore(redisKey, 0, latestSendMessageId);
        Set<Object> sets = zSetOperations.rangeByScore(redisKey, latestSendMessageId, Long.MAX_VALUE);
        Set<MessageInfo> messageInfos = new LinkedHashSet<>();
        Objects.requireNonNull(sets).forEach(o -> messageInfos.add((MessageInfo) o));
        return messageInfos;
    }
}
