package com.tjlcast.sample;

import com.tjlcast.rpc_client.RpcProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by tangjialiang on 2018/5/8.
 */
public class HelloClient3 {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = appContext.getBean(RpcProxy.class);

        int loopCount = 100 ;

        long start = System.currentTimeMillis();

        HelloService helloService = rpcProxy.create(HelloService.class);
        for (int i = 0; i < loopCount; i++) {
            String bupt = helloService.hello("bupt");
        }

        long time = System.currentTimeMillis() - start;

        System.out.println("loop: " + loopCount) ;
        System.out.println("time: " + time + "ms");
        System.out.println("tps: " + (double) loopCount / ((double) time / 1000));
    }
}
