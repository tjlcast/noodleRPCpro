package com.tjlcast.sample;

import com.tjlcast.rpc_client.RpcProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tangjialiang on 2018/5/8.
 */
public class HelloClient4 {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring.xml");
        final RpcProxy rpcProxy = appContext.getBean(RpcProxy.class);

        int threadNum = 10 ;
        int loopCount = 100 ;

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        final CountDownLatch countDownLatch = new CountDownLatch(loopCount);

        try {
            long start = System.currentTimeMillis();

            for (int i = 0; i < loopCount; i++) {
                executorService.submit(new Runnable() {
                    public void run() {
                        HelloService helloService = rpcProxy.create(HelloService.class);
                        String result = helloService.hello("world");

                        System.out.println(result);
                        countDownLatch.countDown();
                    }
                }) ;
            }
            countDownLatch.await();

            long time = System.currentTimeMillis() - start;
            System.out.println("thread: " + threadNum);
            System.out.println("loop: " + loopCount);
            System.out.println("time: " + time + "ms");
            System.out.println("tps: " + (double) loopCount / ((double) time / 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
