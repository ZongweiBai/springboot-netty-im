package com.github.baymin.im.websocketserver.config;

import com.github.baymin.im.websocket.netty.HttpChannelHandler;
import com.github.baymin.im.websocket.netty.WebsocketChannelInitializer;
import com.github.baymin.im.websocket.netty.WebsocketServer;
import com.github.baymin.im.websocket.netty.WebsocketServerHandler;
import com.github.baymin.im.websocket.service.MessageService;
import com.github.baymin.im.websocket.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Websocket相关类自动配置
 *
 * @author Zongwei
 * @date 2019/11/18 14:23
 */
@Configuration
public class WebsocketConfig implements WebMvcConfigurer {

    @Autowired
    private MessageService messageService;

    @Bean
    public HttpChannelHandler httpChannelHandler() {
        return new HttpChannelHandler();
    }

    @Bean
    public WebsocketServerHandler websocketServerHandler() {
        return new WebsocketServerHandler(messageService, idGenerator());
    }

    @Bean
    public WebsocketChannelInitializer websocketChildChannelHandler() {
        return new WebsocketChannelInitializer(websocketServerHandler(), httpChannelHandler());
    }

    @Bean
    public WebsocketServer websocketServer() {
        WebsocketServer websocketServer = new WebsocketServer(websocketChildChannelHandler());
        // 使用新的线程池启动websocket服务
        new Thread(websocketServer).start();
        return websocketServer;
    }

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowedOriginPatterns(CorsConfiguration.ALL)
                .allowCredentials(true);
    }

}
