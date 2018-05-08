package com.tjlcast.rpc_server;

import com.tjlcast.rpc_common.bean.RpcRequest;
import com.tjlcast.rpc_common.bean.RpcResponse;
import com.tjlcast.rpc_common.codec.RpcDecode;
import com.tjlcast.rpc_common.codec.RpcEncode;
import com.tjlcast.rpc_common.util.StringUtil;
import com.tjlcast.rpc_registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangjialiang on 2018/5/5.
 */
public class RpcServer implements ApplicationContextAware, InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcService.class) ;

    private String serviceAddress ;

    private ServiceRegistry serviceRegistry ;

    private HashMap<String, Object> handlerMap = new HashMap<String, Object>() ;

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress ;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress ;
        this.serviceRegistry = serviceRegistry ;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(beansWithAnnotation)) {
            for (Object serviceBean : beansWithAnnotation.values()) {
                RpcService annotation = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = annotation.value().getName() ;
                String serviceVersion = annotation.version();
                if (StringUtil.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion ;
                }
                handlerMap.put(serviceName, serviceBean) ;
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup() ;
        EventLoopGroup workGroup = new NioEventLoopGroup() ;

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup) ;
            bootstrap.channel(NioServerSocketChannel.class) ;
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcDecode(RpcRequest.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncode(RpcResponse.class)); // 编码 RPC 响应
                    pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                }
            }) ;
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024) ;
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true) ;

            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String host = addressArray[0] ;
            int port = Integer.parseInt(addressArray[1]) ;

            ChannelFuture future = bootstrap.bind(host, port).sync();

            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.registry(interfaceName, serviceAddress);
                    LOGGER.debug("registry service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}", port);
            future.channel().closeFuture().sync() ;
        } finally {
            workGroup.shutdownGracefully() ;
            bossGroup.shutdownGracefully() ;
        }
    }
}
