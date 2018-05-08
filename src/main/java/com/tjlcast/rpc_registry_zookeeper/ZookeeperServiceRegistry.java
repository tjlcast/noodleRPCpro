package com.tjlcast.rpc_registry_zookeeper;

import com.tjlcast.rpc_registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tangjialiang on 2018/5/7.
 * 适用于 rpc 服务端进行服务注册
 * zkclient 主要用于动作 {增}
 *          生命周期与serivce相同
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class) ;

    ZkClient zkClient ;

    public ZookeeperServiceRegistry(String zkAddress) {
        // 创建 一个 Zookeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void registry(String serviceName, String serviceAddress) {
        // 创建 registry 节点（永久）
        String zkRegistryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(zkRegistryPath)) {
            zkClient.createPersistent(zkRegistryPath);
            LOGGER.debug("create registry node: {}", zkRegistryPath);
        }

        // 创建 service 节点 (永久)
        String zkServicePath = zkRegistryPath + "/" + serviceName;
        if (!zkClient.exists(zkServicePath)) {
            zkClient.createPersistent(zkServicePath) ;
            LOGGER.debug("create service node: {}", zkServicePath);
        }

        // 创建 address 节点 (临时)
        String zkAddressPath = zkServicePath + "/address-" ;
        String addressNode = zkClient.createEphemeralSequential(zkAddressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }
}
