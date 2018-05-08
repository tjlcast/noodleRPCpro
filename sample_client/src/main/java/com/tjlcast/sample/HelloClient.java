package com.tjlcast.sample;

import com.tjlcast.rpc_client.RpcProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by tangjialiang on 2018/5/8.
 */
public class HelloClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = classPathXmlApplicationContext.getBean(RpcProxy.class);

        HelloService helloService = (HelloService)rpcProxy.create(HelloService.class);
        String result = helloService.hello("world");
        System.out.println(result) ;

        HelloService helloService1 = rpcProxy.create(HelloService.class, "sample.hello1");
        String result2 = helloService1.hello("世界");
        System.out.println(result2) ;

        System.exit(0);
    }
}
