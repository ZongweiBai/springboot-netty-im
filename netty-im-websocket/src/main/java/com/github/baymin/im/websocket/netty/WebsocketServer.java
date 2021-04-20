package com.github.baymin.im.websocket.netty;

import com.github.baymin.im.websocket.util.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * Netty WebSocket服务器
 *
 * @author Zongwei
 * @date 2019/11/15 10:00
 */
@Slf4j
@NoArgsConstructor
public class WebsocketServer implements Runnable {

    /**
     * boss辅助客户端的tcp连接请求
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    /**
     * worker负责与客户端之间的读写操作
     */
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    private ChannelFuture serverChannelFuture;

    private ChannelHandler childChannelHandler;

    public WebsocketServer(WebsocketChannelInitializer websocketChannelInitializer) {
        this.childChannelHandler = websocketChannelInitializer;
    }

    @Override
    public void run() {
        startNettyServer();
    }

    private void startNettyServer() {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workGroup)
                    // 配置客户端的channel类型
                    .channel(NioServerSocketChannel.class)
                    // 配置TCP参数，握手字符串长度设置
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 禁用禁用nagle算法（尽可能发送大块数据，减少充斥的小块数据）
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 配置固定长度接收缓存区分配器
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(Constant.RCVBUF_ALLOCATOR_SIZE))
                    // 绑定I/O事件的处理类, {@link WebsocketChannelInitializer}中定义
                    .childHandler(childChannelHandler);

            // 异步的绑定服务器，调用sync()方法阻塞等待直到绑定完成
            serverChannelFuture = serverBootstrap.bind(Constant.WEBSOCKET_PORT).sync();
            // 获取Channel的CloseFuture，并且阻塞当前线程直到它完成
            serverChannelFuture.channel().closeFuture().sync();

            stopWatch.stop();
            log.info("Netty Websocket服务器启动完成，耗时 {} ms，已绑定端口 {} 阻塞式等候客户端连接", stopWatch.getTotalTimeMillis(), Constant.WEBSOCKET_PORT);
        } catch (Exception e) {
            log.error("Netty Websocket服务器启动失败", e);
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    /**
     * 描述：关闭Netty Websocket服务器，主要是释放连接
     * 连接包括：服务器连接serverChannel，
     * 客户端TCP处理连接bossGroup，
     * 客户端I/O操作连接workerGroup
     *
     * 若只使用
     * bossGroupFuture = bossGroup.shutdownGracefully();
     * workerGroupFuture = workerGroup.shutdownGracefully();
     * 会造成内存泄漏。
     */
    public void close() {
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workGroupFuture = workGroup.shutdownGracefully();

        try {
            bossGroupFuture.await();
            workGroupFuture.await();
        } catch (InterruptedException e) {
            log.error("关闭Netty Websocket服务器出现异常", e);
        }
    }
}
