package com.github.baymin.im.websocketserver.config;

import com.github.baymin.im.websocket.registry.ServiceInstance;
import com.github.baymin.im.websocket.registry.ServiceRegistry;
import com.github.baymin.im.websocketserver.repository.GroupInfoDao;
import com.github.baymin.im.websocketserver.repository.UserInfoDao;
import com.github.baymin.im.websocketserver.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.baymin.im.websocket.util.Constant.WEBSOCKET_PORT;

/**
 * 监听springboot启动完成
 *
 * @author Zongwei
 * @date 2020/4/18 17:21
 */
@Component
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {

//    @Autowired
//    private ServiceRegistry serviceRegistry;

    @Autowired
    private GroupInfoDao groupInfoDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.client.ip-address}")
    private String ipAddress;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        ServiceInstance serviceInstance = new ServiceInstance("WS-" + applicationName, SystemUtils.getHostIp(), WEBSOCKET_PORT);
        serviceInstance.setServiceId(serviceInstance.getServiceName() + "-" + WEBSOCKET_PORT + ":" + ipAddress + ":" + WEBSOCKET_PORT);
        serviceInstance.setSimpleName(serviceInstance.getServiceName());
        serviceInstance.setTags(List.of("聊天服务", "secure=false"));
        if (applicationEvent instanceof ApplicationReadyEvent) {
            // 应用已启动，注册服务到consul
//            serviceRegistry.register(serviceInstance);

            // 加载用户信息和群组信息
            userInfoDao.loadUserInfo();
            groupInfoDao.loadGroupInfo();
        }
        // 应用停止或者关闭，注销consul的服务
        if (applicationEvent instanceof ContextStoppedEvent || applicationEvent instanceof ContextClosedEvent) {
//            serviceRegistry.deregister(serviceInstance);
        }
    }
}
