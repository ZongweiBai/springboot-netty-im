package com.github.baymin.im.websocketserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 群组信息
 * @author Zongwei
 */
@Data
@NoArgsConstructor
public class GroupInfo {

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 群组名
     */
    private String groupName;

    /**
     * 群图标
     */
    private String groupAvatarUrl;

    /**
     * 群成员
     */
    private Set<UserInfo> members;

    /**
     * 群成员ID
     */
    private Set<Object> memberIds;

    public GroupInfo(String groupId, String groupName, String groupAvatarUrl, Set<UserInfo> members) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupAvatarUrl = groupAvatarUrl;
        this.members = members;
    }

}
