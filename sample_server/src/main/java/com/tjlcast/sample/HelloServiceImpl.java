package com.tjlcast.sample;

import com.tjlcast.rpc_server.RpcService;

/**
 * Created by tangjialiang on 2018/5/8.
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{

    public String hello(String name) {
        return "hello! " + name;
    }

    public String hello(Person person) {
        return "hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
