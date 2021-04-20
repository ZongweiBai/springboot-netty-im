package com.github.baymin.im.websocket.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务实例
 *
 * @author Zongwei
 * @date 2020/4/18 17:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstance {

    private String serviceId;
    private String serviceName;
    private String simpleName;
    private String address;
    private int port;
    private List<String> tags;

    public ServiceInstance(String serviceName, String address, int port) {
        this.serviceName = serviceName;
        this.address = address;
        this.port = port;
    }

}
