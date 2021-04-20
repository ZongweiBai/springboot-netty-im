package com.github.baymin.im.websocketserver.service;

import com.github.baymin.im.websocketserver.entity.GroupInfo;

/**
 * Group操作服务接口类
 *
 * @author zongwei
 */
public interface GroupService {

    /**
     * 根据groupId获取群组信息
     */
    GroupInfo getByGroupId(String groupId);

}
