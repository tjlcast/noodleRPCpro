package com.tjlcast.rpc_common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * 序列化工具 (基于 protostuff 和 Objenesis)
 *
 * 其中 protostuff
 *      -   ProtobufIOUtil.toByteArray
 *      -   ProtobufIOUtil.mergeFrom
 *
 * 其中 Objenesis
 *      -   主要基于 Clazz 字节码构建一个'空'对象, protostuff会把字节码通过schema生成 对象实例。
 */
public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>() ;

    private static Objenesis objenesis = new ObjenesisStd(true) ;

    private SerializationUtil() {}

    /**
     * 序列化 (对象 -> 字节数组)
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls) ;
            return ProtobufIOUtil.toByteArray(obj, schema, buffer) ;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e) ;
        } finally {
            buffer.clear() ;
        }
    }

    /**
     * 反序列化 (字符数组 -> 对象)
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(data, message, schema);
            return message ;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e) ;
        }
    }

    /**
     * 由上面两个函数调用，serialize and deserialize
     * @param cls
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>)cachedSchema.getOrDefault(cls.getClass(),
                RuntimeSchema.createFrom(cls));
        return schema ;
    }
}
