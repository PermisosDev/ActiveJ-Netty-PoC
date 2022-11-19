package me.permisos.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Main  {
    public static void main(String[] args) {
        new Main();
    }
    public Main() {
        run();
    }
    public void run() {
        try {
            System.out.println("Starting client");
            Bootstrap clientBootstrap = new Bootstrap()
                    .handler(new ClientHandler())
                    .handler(new MessageHandler())
                    .channel(NioSocketChannel.class)
                    .group(new NioEventLoopGroup());

            Channel channel = clientBootstrap.connect("127.0.0.1", 1337).channel();
            channel.writeAndFlush(toByteBuf("PING")).sync().addListener((e) -> {
                System.out.println("Message sent: " + e.toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> io.netty.buffer.ByteBuf toByteBuf(T value) {
        return Unpooled.wrappedBuffer(value.toString().getBytes());
    }
}
