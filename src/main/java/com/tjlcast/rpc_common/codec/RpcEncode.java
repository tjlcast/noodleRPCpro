package com.tjlcast.rpc_common.codec;

import com.tjlcast.rpc_common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by tangjialiang on 2018/5/7.
 *
 * netty's 编码器
 */
public class RpcEncode extends MessageToByteEncoder {

    private Class<?> genericClass ;

    public RpcEncode(Class<?> genericClass) {
        this.genericClass = genericClass ;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object messsage, ByteBuf out) throws Exception {
        if (genericClass.isInstance(messsage)) {
            byte[] serializedMessage = SerializationUtil.serialize(messsage) ;
            out.writeInt(serializedMessage.length) ;
            out.writeBytes(serializedMessage) ;
        }
    }
}
