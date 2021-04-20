package com.github.baymin.im.websocketserver.controller;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocket.util.Constant;
import com.github.baymin.im.websocketserver.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 首页Controller
 *
 * @author zongwei
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private SecurityService securityService;

    @RequestMapping(method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInfo login(HttpSession session,
                              @RequestParam String username,
                              @RequestParam String password) {
        return securityService.login(username, password, session);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInfo getUserInfo(HttpSession session) {
        return securityService.logout(session);
    }

}
