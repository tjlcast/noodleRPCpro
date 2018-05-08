package com.tjlcast.rpc_common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tangjialiang on 2018/5/5.
 *
 * 字符util
 */
public class StringUtil {
    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            str = str.trim() ;
        }
        return StringUtils.isEmpty(str) ;
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str) ;
    }

    /**
     *
     * @param str
     * @param separator
     * @return
     */
    public static String[] split(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator) ;
    }
}
