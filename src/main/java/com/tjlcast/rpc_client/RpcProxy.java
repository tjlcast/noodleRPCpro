package com.tjlcast.rpc_client;

import com.tjlcast.rpc_common.bean.RpcRequest;
import com.tjlcast.rpc_common.bean.RpcResponse;
import com.tjlcast.rpc_common.util.StringUtil;
import com.tjlcast.rpc_registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * RPC 代理（用于创建 RPC 服务代理）
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class) ;

    private String serviceAddress ;

    private ServiceDiscovery serviceDiscovery ;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress ;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建 RPC 请求对象并设置请求属性
                        RpcRequest request = new RpcRequest() ;
                        request.setRequestId(UUID.randomUUID().toString()) ;
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setParameterType(method.getParameterTypes());
                        request.setParameter(args);

                        // 获取 RPC 服务地址
                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName() ;
                            if (StringUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion ;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName) ;
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }

                        if (StringUtil.isEmpty(serviceAddress)) {
                            throw new RuntimeException("server address is empty") ;
                        }

                        // 从 RPC 服务地址从解析主机名与端口号
                        String[] array = StringUtil.split(serviceAddress, ":") ;
                        String host = array[0] ;
                        int port = Integer.parseInt(array[1]) ;

                        // 创建 RPC 客户端口对象并发送 RPC 请求
                        RpcClient rpcClient = new RpcClient(host, port) ;
                        long time = System.currentTimeMillis();
                        RpcResponse response = rpcClient.send(request);
                        LOGGER.debug("time: {}lms", System.currentTimeMillis() - time);

                        if (response == null) {
                            throw new RuntimeException("response is null") ;
                        }
                        // 返回 RPC 响应结果
                        if (response.hashException()) {
                            throw response.getException() ;
                        } else {
                            return response.getResult() ;
                        }
                    }
                }
        ) ;
    }
}
