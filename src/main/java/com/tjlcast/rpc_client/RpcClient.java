package com.tjlcast.rpc_client;

import com.tjlcast.rpc_common.bean.RpcRequest;
import com.tjlcast.rpc_common.bean.RpcResponse;
import com.tjlcast.rpc_common.codec.RpcDecode;
import com.tjlcast.rpc_common.codec.RpcEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * RPC 客户端（用于发送 RPC 请求）
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class) ;

    private final String host ;
    private final int port ;

    private RpcResponse rpcResponse ;

    public RpcClient(String host, int port) {
        this.host = host ;
        this.port = port ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close() ;
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup() ;

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) ;
            bootstrap.channel(NioSocketChannel.class) ;
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncode(RpcRequest.class)) ;
                    pipeline.addLast(new RpcDecode(RpcResponse.class)) ;
                    pipeline.addLast(RpcClient.this) ;
                }
            }) ;
            bootstrap.option(ChannelOption.TCP_NODELAY, true) ;
            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(host, port).sync() ;
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync() ;
            channel.closeFuture().sync() ;
            // 返回 RPC 响应对象
            return rpcResponse ;
        } finally {
            group.shutdownGracefully() ;
        }
    }
}
