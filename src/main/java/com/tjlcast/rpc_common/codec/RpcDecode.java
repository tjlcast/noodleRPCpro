package com.tjlcast.rpc_common.codec;

import com.tjlcast.rpc_common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * netty's 解码器
 */
public class RpcDecode extends ByteToMessageDecoder{

    private Class<?> genericClass ;

    public RpcDecode(Class<?> genericClass) {
        this.genericClass = genericClass ;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return ;
        }

        // 确定收到一个对象的长度的时候设置下标记录
        in.markReaderIndex() ;
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 如果没有收到长度大小的数据则把下标还原
            in.resetReaderIndex() ;
            return ;
        }

        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes) ;
        out.add(SerializationUtil.deserialize(bytes, genericClass)) ;
    }
}
