package com.github.baymin.im.websocketserver.service.impl;

import com.github.baymin.im.websocketserver.entity.GroupInfo;
import com.github.baymin.im.websocketserver.entity.UserInfo;
import com.github.baymin.im.websocketserver.repository.GroupInfoDao;
import com.github.baymin.im.websocketserver.repository.UserInfoDao;
import com.github.baymin.im.websocketserver.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private GroupInfoDao groupInfoDao;

    @Override
    public UserInfo getByUserId(String userId) {
        UserInfo userInfo = userInfoDao.getByUserId(userId);

        if (Objects.isNull(userInfo)) {
            return null;
        }
        if (!CollectionUtils.isEmpty(userInfo.getGroupIds())) {
            Set<GroupInfo> groupInfos = new LinkedHashSet<>(userInfo.getGroupIds().size());
            userInfo.getGroupIds().forEach(groupId -> {
                groupInfos.add(groupInfoDao.getBaseInfoById(groupId.toString()));
            });
            userInfo.setGroupList(groupInfos);
        }
        return userInfo;
    }

}
