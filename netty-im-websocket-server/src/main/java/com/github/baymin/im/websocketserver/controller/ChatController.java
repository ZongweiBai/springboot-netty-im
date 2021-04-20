package com.github.baymin.im.websocketserver.controller;

import com.github.baymin.im.websocket.util.Constant;
import com.github.baymin.im.websocketserver.entity.UserInfo;
import com.github.baymin.im.websocketserver.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author zongwei
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 描述：登录成功后，调用此接口进行页面跳转
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toChatRoom() {
//        return "chatroom";
        return "chatroom-vue";
    }

    /**
     * 描述：登录成功跳转页面后，调用此接口获取用户信息
     */
    @RequestMapping(value = "/user_info", method = RequestMethod.POST)
    @ResponseBody
    public UserInfo getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String) userId);
    }

}
