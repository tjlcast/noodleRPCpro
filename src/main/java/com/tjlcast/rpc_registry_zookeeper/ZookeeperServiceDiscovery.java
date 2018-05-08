package com.tjlcast.rpc_registry_zookeeper;

import com.tjlcast.rpc_common.util.CollectionUtil;
import com.tjlcast.rpc_registry.ServiceDiscovery;
import io.netty.util.internal.ThreadLocalRandom;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * 适用于 rpc 客户端进行服务选择
 * zkclient 主要用于动作 {查}
 *          生命周期为一次服务选择
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class) ;

    private String zkAddress ;

    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress ;
    }

    @Override
    public String discover(String serviceName) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName ;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath)) ;
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address on path: %s", servicePath)) ;
            }

            // 获取 address 节点
            String address ;
            int size = addressList.size() ;
            if (size == 1) {
                // 若存在一个地址，则直接使用这个地址
                address = addressList.get(0) ;
                LOGGER.debug("get only address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}", address);
            }

            // 获取 address 节点的值
            String dataPath = servicePath + "/" + address;
            return zkClient.readData(dataPath);
        } finally {
            zkClient.close();
        }
    }
}
