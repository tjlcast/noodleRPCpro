package com.tjlcast.rpc_registry;

/**
 * Created by tangjialiang on 2018/5/5.
 */
public interface ServiceRegistry {

    void registry(String serviceName, String serviceAddress) ;
}
