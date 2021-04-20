package com.github.baymin.im.websocketserver.registry;

import com.ecwid.consul.ConsulException;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.github.baymin.im.websocket.registry.ServiceInstance;
import com.github.baymin.im.websocket.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.stereotype.Component;

/**
 * 注册到consul上
 *
 * @author Zongwei
 * @date 2020/4/18 17:12
 */
@Slf4j
//@Component
public class ImConsulServiceRegistry implements ServiceRegistry {

    @Value("${spring.cloud.consul.discovery.health-check-url}")
    private String healthCheckUrl;

    @Autowired
    private ConsulClient consulClient;

    @Autowired
    private ConsulDiscoveryProperties consulDiscoveryProperties;

    @Autowired
    private ConsulServiceRegistry consulServiceRegistry;

    @Override
    public void register(ServiceInstance instance) {

        NewService service = new NewService();
        service.setAddress(instance.getAddress());
        service.setId(instance.getServiceId());
        service.setName(instance.getServiceName());
        service.setPort(instance.getPort());
        service.setTags(instance.getTags());

        NewService.Check check = new NewService.Check();
        check.setHttp(healthCheckUrl);
        check.setInterval("10s");
        check.setTimeout("30s");
        check.setDeregisterCriticalServiceAfter("30s");

        service.setCheck(check);

        log.info("Registering service with consul: {}", service.getName());
        try {
            ConsulRegistration registration = new ConsulRegistration(service, consulDiscoveryProperties);
            consulServiceRegistry.register(registration);
        } catch (ConsulException e) {
            log.warn("Error registering service with consul: {}, error: [}", instance.getServiceName(), e);
        }
    }

    @Override
    public void deregister(ServiceInstance instance) {
        log.info("de_registering service with consul: {}", instance.getServiceId());
        consulClient.agentServiceDeregister(instance.getServiceId());
    }

    @Override
    public void close() {
    }
}
