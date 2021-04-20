package com.github.baymin.im.websocketserver.repository;

import com.github.baymin.im.websocketserver.entity.UserInfo;

/**
 * @author zongwei
 */
public interface UserInfoDao {

    void loadUserInfo();

    UserInfo getByUsername(String username);

    UserInfo getByUserId(String userId);

    UserInfo getUserBaseInfo(String userId);
}
