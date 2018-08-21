package com.tjlcast.rpc_registry_zookeeper;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * zk 的文件系统:
 *  /registry/
 *          + service1//address-111
 *                     /address-112
 *          + service2/address-111
 *                    /address-113
 *          + service3/...
 */
public interface Constant {
    String ZK_REGISTRY_PATH = "/registry" ;

    int ZK_SESSION_TIMEOUT = 5000 ;

    int ZK_CONNECTION_TIMEOUT = 1000 ;
}
