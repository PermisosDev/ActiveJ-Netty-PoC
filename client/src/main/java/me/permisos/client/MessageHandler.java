package me.permisos.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object message){
        ByteBuf buf = (ByteBuf) message;
        try {
            if(buf.refCnt() <= 0 || buf.readableBytes() <= 1) return;
            String s = buf.readCharSequence(buf.readableBytes(), StandardCharsets.UTF_8).toString();
            System.out.println("MessageHandler.channelRead: " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
