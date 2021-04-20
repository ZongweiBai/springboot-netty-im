package com.github.baymin.im.websocketserver.service.impl;

import com.github.baymin.im.websocketserver.entity.GroupInfo;
import com.github.baymin.im.websocketserver.entity.UserInfo;
import com.github.baymin.im.websocketserver.repository.GroupInfoDao;
import com.github.baymin.im.websocketserver.repository.UserInfoDao;
import com.github.baymin.im.websocketserver.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 群组service实现类
 *
 * @author Zongwei
 * @date 2019/11/18 14:15
 */
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupInfoDao groupInfoDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public GroupInfo getByGroupId(String groupId) {
        GroupInfo groupInfo = groupInfoDao.getByGroupId(groupId);

        if (!CollectionUtils.isEmpty(groupInfo.getMemberIds())) {

            Set<UserInfo> memberInfos = new LinkedHashSet<>(groupInfo.getMemberIds().size());
            groupInfo.getMemberIds().forEach(memberId -> {
                memberInfos.add(userInfoDao.getUserBaseInfo(memberId.toString()));
            });
            groupInfo.setMembers(memberInfos);
        }
        return groupInfo;
    }

}
