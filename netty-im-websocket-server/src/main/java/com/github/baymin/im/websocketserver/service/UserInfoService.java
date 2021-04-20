package com.github.baymin.im.websocketserver.service;

import com.github.baymin.im.websocketserver.entity.UserInfo;

/**
 * @author zongwei
 */
public interface UserInfoService {

    UserInfo getByUserId(String userId);
}
