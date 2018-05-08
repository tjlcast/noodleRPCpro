package com.tjlcast.sample;


import com.tjlcast.rpc_server.RpcService;

/**
 * Created by tangjialiang on 2018/5/8.
 */

@RpcService(value = HelloService.class, version = "sample.hello1")
public class HelloServiceImpl1 implements HelloService {

    public String hello(String name) {
        return "你好! " + name;
    }

    public String hello(Person person) {
        return "你好! " + person.getFirstName() + " " + person.getLastName();
    }
}