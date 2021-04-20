package com.github.baymin.im.websocket.registry;

/**
 * 服务注册
 *
 * @author Zongwei
 * @date 2020/4/18 17:12
 */
public interface ServiceRegistry {

    void register(ServiceInstance instance);

    void deregister(ServiceInstance instance);

    void close();

}
