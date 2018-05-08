package com.tjlcast.rpc_server;

import com.tjlcast.rpc_common.bean.RpcRequest;
import com.tjlcast.rpc_common.bean.RpcResponse;
import com.tjlcast.rpc_common.util.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by tangjialiang on 2018/5/5.
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class) ;

    private final Map<String, Object> handleMap ;

    public RpcServerHandler(Map<String, Object> handleMap) {
        this.handleMap = handleMap ;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // 创建并初始化 RPC 响应对象
        RpcResponse response = new RpcResponse() ;
        response.setRequestId(rpcRequest.getRequestId());

        // 处理 RPC 请求
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error(String.format("handle result failure: %s", e)) ;
            response.setException(e);
        }

        // 写入 RPC 响应对象并自动关闭连接
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE) ;
    }

    private Object handle(RpcRequest rpcRequest) throws InvocationTargetException {
        // 获得服务对象
        String serviceName = rpcRequest.getInterfaceName() ;
        String serviceVersion = rpcRequest.getServiceVersion() ;
        if (StringUtil.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion ;
        }

        Object serviceBean = handleMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service by key: %s", serviceName)) ;
        }

        // 获取反射调用需要的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterType = rpcRequest.getParameterType();
        Object[] parameter = rpcRequest.getParameter();

        // 使用 CGLib 执行反射调用
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterType);
        return serviceFastMethod.invoke(serviceBean, parameter);
    }
}
