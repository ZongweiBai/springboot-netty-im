package com.github.baymin.im.websocketserver.entity;

import lombok.Data;

import java.util.Set;

/**
 * 用户信息
 *
 * @author Zongwei
 */
@Data
public class UserInfo {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 好友列表
     */
    private Set<UserInfo> friendList;

    /**
     * 群组列表
     */
    private Set<GroupInfo> groupList;

    /**
     * 群组ID
     */
    private Set<Object> groupIds;

    public UserInfo() {
        super();
    }

    public UserInfo(String userId, String username, String password, String avatarUrl) {
        super();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

}
