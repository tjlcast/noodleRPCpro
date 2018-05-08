package com.tjlcast.rpc_common.util;

import org.apache.commons.collections4.MapUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by tangjialiang on 2018/5/7.
 */
public class CollectionUtil {

    /**
     * 判断 Collection 是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection) ;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection) ;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map) ;
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map) ;
    }
}
