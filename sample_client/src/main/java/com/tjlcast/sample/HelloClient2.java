package com.tjlcast.sample;

import com.tjlcast.rpc_client.RpcProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by tangjialiang on 2018/5/8.
 */
public class HelloClient2 {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = appContext.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String hello = helloService.hello(new Person("tjl", "cast"));
        System.out.println(hello);

        System.exit(0);
    }
}
