package com.github.baymin.im.websocket.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.github.baymin.im.websocket.util.Constant.WebsocketServerConfig.*;

/**
 * 初始化ChannelChildHandler连接
 *
 * @author Zongwei
 * @date 2019/11/15 10:00
 */
@NoArgsConstructor
public class WebsocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private WebsocketServerHandler websocketServerHandler;

    private HttpChannelHandler httpChannelHandler;

    public WebsocketChannelInitializer(WebsocketServerHandler websocketServerHandler, HttpChannelHandler httpChannelHandler) {
        this.websocketServerHandler = websocketServerHandler;
        this.httpChannelHandler = httpChannelHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // HTTP编码解码器
                .addLast("http-codec", new HttpServerCodec())
                // ChunkedWriteHandler：向客户端发送HTML5文件
                .addLast("http-chunked", new ChunkedWriteHandler())
                // 把HTTP头、HTTP体拼成完整的HTTP请求
                .addLast("aggregator", new HttpObjectAggregator(MAX_AGGREGATED_CONTENT_LENGTH))
                // 在管道中添加我们自己的接收数据实现方法
                // httpChannelHandler和websocketServerHandler都被标注为@Sharable，所以我们可以总是使用同样的实例
//                .addLast("http-handler", httpChannelHandler)
                .addLast(new WebSocketServerProtocolHandler("/ws-chat",null,true,65535))
                // 增加心跳检测支持
                .addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0))
                .addLast("websocket-handler", websocketServerHandler);
    }

}
