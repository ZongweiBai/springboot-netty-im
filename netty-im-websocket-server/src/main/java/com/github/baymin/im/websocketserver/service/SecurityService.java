package com.github.baymin.im.websocketserver.service;

import com.github.baymin.im.websocket.domain.ResponseInfo;

import javax.servlet.http.HttpSession;

/**
 * @author zongwei
 */
public interface SecurityService {

    ResponseInfo login(String username, String password, HttpSession session);

    ResponseInfo logout(HttpSession session);

}
