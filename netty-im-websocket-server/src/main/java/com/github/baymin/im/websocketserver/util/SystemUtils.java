package com.github.baymin.im.websocketserver.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Baymin on 2017/8/23.
 */
@Slf4j
public class SystemUtils {

    public static InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Got error while get local host, error message : {}", e.getMessage());
        }
        return null;
    }

    public static String getHostIp() {
        InetAddress inetAddress = getInetAddress();
        return inetAddress.getHostAddress();
    }

    public static String getHostName() {
        InetAddress inetAddress = getInetAddress();
        return inetAddress.getHostName();
    }

}
