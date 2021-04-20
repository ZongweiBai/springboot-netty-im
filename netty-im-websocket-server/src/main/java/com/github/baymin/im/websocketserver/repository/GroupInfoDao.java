package com.github.baymin.im.websocketserver.repository;


import com.github.baymin.im.websocketserver.entity.GroupInfo;

/**
 * @author zongwei
 */
public interface GroupInfoDao {

    void loadGroupInfo();

    GroupInfo getByGroupId(String groupId);

    GroupInfo getBaseInfoById(String groupId);
}
