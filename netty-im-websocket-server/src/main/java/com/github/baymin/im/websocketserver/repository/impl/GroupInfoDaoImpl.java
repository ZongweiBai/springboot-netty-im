package com.github.baymin.im.websocketserver.repository.impl;

import com.github.baymin.im.websocketserver.entity.GroupInfo;
import com.github.baymin.im.websocketserver.repository.GroupInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

/**
 * 群组数据库操作类
 *
 * @author zongwei
 */
@Repository
public class GroupInfoDaoImpl implements GroupInfoDao {

    /**
     * 记录群组基本信息的key，Redis类型：hashMap
     */
    private static final String GROUP_INFO_KEY = "nettyim:group:info:%s";

    /**
     * 记录群组成员ID的key，Redis类型：set
     */
    private static final String GROUP_MEMBER_KEY = "nettyim:group:member:%s";

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private SetOperations<String, Object> setOperations;

    @Override
    public void loadGroupInfo() {
        GroupInfo groupInfo = new GroupInfo("G_001", "Group01", "img/avatar/Group01.jpg", null);
        // 初始化群组基本信息
        String groupInfoKey = String.format(GROUP_INFO_KEY, groupInfo.getGroupId());
        hashOperations.put(groupInfoKey, "groupId", groupInfo.getGroupId());
        hashOperations.put(groupInfoKey, "groupName", groupInfo.getGroupName());
        hashOperations.put(groupInfoKey, "avatar", groupInfo.getGroupAvatarUrl());

        // 初始化群组成员信息
        setOperations.add(String.format(GROUP_MEMBER_KEY, groupInfo.getGroupId()), new String[]{"001", "002", "003", "004", "005", "006", "007", "008", "009"});
    }

    @Override
    public GroupInfo getByGroupId(String groupId) {
        GroupInfo groupInfo = this.getBaseInfoById(groupId);
        if (Objects.isNull(groupInfo)) {
            return null;
        }

        // 成员信息
        Set<Object> memberIds = setOperations.members(String.format(GROUP_MEMBER_KEY, groupId));
        groupInfo.setMemberIds(memberIds);
        return groupInfo;
    }

    @Override
    public GroupInfo getBaseInfoById(String groupId) {
        String groupInfoKey = String.format(GROUP_INFO_KEY, groupId);
        Object groupIdObj = hashOperations.get(groupInfoKey, "groupId");
        if (Objects.isNull(groupIdObj)) {
            return null;
        }

        GroupInfo result = new GroupInfo();
        result.setGroupId(groupId);
        result.setGroupName(String.valueOf(hashOperations.get(groupInfoKey, "groupName")));
        result.setGroupAvatarUrl(String.valueOf(hashOperations.get(groupInfoKey, "avatar")));
        return result;
    }
}
