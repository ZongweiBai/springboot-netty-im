package com.github.baymin.im.websocketserver.repository.impl;

import com.github.baymin.im.websocketserver.entity.UserInfo;
import com.github.baymin.im.websocketserver.repository.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户数据库操作类
 *
 * @author zongwei
 */
@Repository
public class UserInfoDaoImpl implements UserInfoDao {

    /**
     * 记录用户基本信息的key，Redis类型：hashMap
     */
    private static final String USER_INFO_KEY = "nettyim:user:info:%s";

    /**
     * 记录用户ID和用户名的map对照，Redis类型：hashMap
     */
    private static final String USER_ID_NAME_MAP_KEY = "nettyim:user:mapping:name-id";

    /**
     * 记录用户好友信息的key，Redis类型：set
     */
    private static final String USER_FRIEND_KEY = "nettyim:user:friend:%s";

    /**
     * 记录用户群信息的key，Redis类型：set
     */
    private static final String USER_GROUP_KEY = "nettyim:user:group:%s";

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private SetOperations<String, Object> setOperations;

    /**
     * 这里使用死数据，不使用数据库
     */
    @Override
    public void loadUserInfo() {
        List<UserInfo> allUserInfoList = generateFriendList("");
        allUserInfoList.forEach(userInfo -> {
            // 初始化用户基本信息
            String userInfoKey = String.format(USER_INFO_KEY, userInfo.getUserId());
            hashOperations.put(userInfoKey, "userId", userInfo.getUserId());
            hashOperations.put(userInfoKey, "userName", userInfo.getUsername());
            hashOperations.put(userInfoKey, "password", userInfo.getPassword());
            hashOperations.put(userInfoKey, "avatar", userInfo.getAvatarUrl());

            // 保存用户名和ID的关系
            hashOperations.put(USER_ID_NAME_MAP_KEY, userInfo.getUsername(), userInfo.getUserId());

            // 初始化用户好友信息
            List<UserInfo> friends = generateFriendList(userInfo.getUserId());
            String[] friendIds = friends.stream().map(UserInfo::getUserId).collect(Collectors.toSet()).toArray(new String[friends.size() - 1]);
            setOperations.add(String.format(USER_FRIEND_KEY, userInfo.getUserId()), friendIds);

            // 初始化用户群信息
            setOperations.add(String.format(USER_GROUP_KEY, userInfo.getUserId()), "001");
        });
    }

    @Override
    public UserInfo getByUsername(String username) {
        Object userIdObj = hashOperations.get(USER_ID_NAME_MAP_KEY, username);
        if (Objects.isNull(userIdObj)) {
            return null;
        }
        return this.getByUserId(userIdObj.toString());
    }

    @Override
    public UserInfo getByUserId(String userId) {

        // 获取基本信息
        UserInfo result = getUserBaseInfo(userId);
        if (Objects.isNull(result)) {
            return null;
        }

        // 好友信息
        Set<Object> friendIds = setOperations.members(String.format(USER_FRIEND_KEY, userId));
        if (!CollectionUtils.isEmpty(friendIds)) {
            Set<UserInfo> friendInfos = new LinkedHashSet<>(friendIds.size());
            friendIds.forEach(friendId -> friendInfos.add(getUserBaseInfo(friendId.toString())));
            result.setFriendList(friendInfos);
        }

        // 群信息
        Set<Object> groupIds = setOperations.members(String.format(USER_GROUP_KEY, userId));
        result.setGroupIds(groupIds);

        return result;
    }

    /**
     * 获取用户基本信息
     *
     * @param userId 用户ID
     * @return UserInfo
     */
    @Override
    public UserInfo getUserBaseInfo(String userId) {
        String userInfoKey = String.format(USER_INFO_KEY, userId);
        Object userIdObj = hashOperations.get(userInfoKey, "userId");
        if (Objects.isNull(userIdObj)) {
            return null;
        }

        UserInfo result = new UserInfo();
        result.setUserId(userId);
        result.setUsername(String.valueOf(hashOperations.get(userInfoKey, "userName")));
        result.setPassword(String.valueOf(hashOperations.get(userInfoKey, "password")));
        result.setAvatarUrl(String.valueOf(hashOperations.get(userInfoKey, "avatar")));
        return result;
    }

    /**
     * 生成用户信息
     *
     * @param userId 要排除的用户ID
     * @return List<UserInfo>
     */
    private List<UserInfo> generateFriendList(String userId) {
        UserInfo userInfo = new UserInfo("001", "Member001", "001", "img/avatar/Member001.jpg");
        UserInfo userInfo2 = new UserInfo("002", "Member002", "002", "img/avatar/Member002.jpg");
        UserInfo userInfo3 = new UserInfo("003", "Member003", "003", "img/avatar/Member003.jpg");
        UserInfo userInfo4 = new UserInfo("004", "Member004", "004", "img/avatar/Member004.jpg");
        UserInfo userInfo5 = new UserInfo("005", "Member005", "005", "img/avatar/Member005.jpg");
        UserInfo userInfo6 = new UserInfo("006", "Member006", "006", "img/avatar/Member006.jpg");
        UserInfo userInfo7 = new UserInfo("007", "Member007", "007", "img/avatar/Member007.jpg");
        UserInfo userInfo8 = new UserInfo("008", "Member008", "008", "img/avatar/Member008.jpg");
        UserInfo userInfo9 = new UserInfo("009", "Member009", "009", "img/avatar/Member009.jpg");
        List<UserInfo> friendList = new ArrayList<>();
        friendList.add(userInfo);
        friendList.add(userInfo2);
        friendList.add(userInfo3);
        friendList.add(userInfo4);
        friendList.add(userInfo5);
        friendList.add(userInfo6);
        friendList.add(userInfo7);
        friendList.add(userInfo8);
        friendList.add(userInfo9);
        friendList.removeIf(entry -> userId.equals(entry.getUserId()));
        return friendList;
    }


}
