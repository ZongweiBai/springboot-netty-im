package com.github.baymin.im.websocketserver.service.impl;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocketserver.entity.UserInfo;
import com.github.baymin.im.websocket.util.Constant;
import com.github.baymin.im.websocketserver.repository.UserInfoDao;
import com.github.baymin.im.websocketserver.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;

/**
 * @author zongwei
 */
@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public ResponseInfo login(String username, String password, HttpSession session) {
        UserInfo userInfo = userInfoDao.getByUsername(username);
        if (userInfo == null) {
            return new ResponseInfo().error("不存在该用户名");
        }
        if (!userInfo.getPassword().equals(password)) {
            return new ResponseInfo().error("密码不正确");
        }
        session.setAttribute(Constant.USER_TOKEN, userInfo.getUserId());
        return new ResponseInfo().success();
    }

    @Override
    public ResponseInfo logout(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        if (userId == null) {
            return new ResponseInfo().error("请先登录！");
        }
        session.removeAttribute(Constant.USER_TOKEN);
        log.info(MessageFormat.format("userId为 {0} 的用户已注销登录!", userId));
        return new ResponseInfo().success();
    }
}
