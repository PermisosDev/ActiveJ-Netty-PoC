package me.permisos.server;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufStrings;
import io.activej.csp.ChannelSupplier;
import io.activej.csp.binary.BinaryChannelSupplier;
import io.activej.csp.binary.ByteBufsDecoder;
import io.activej.eventloop.Eventloop;
import io.activej.net.SimpleServer;
import io.activej.promise.Promises;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;


public class Main {
    private static final int PORT = 1337;
    private static final String ADDRESS = "127.0.0.1";
    private static final InetSocketAddress ADDR = new InetSocketAddress(ADDRESS, PORT);

    private static final ByteBufsDecoder<String> DECODER = ByteBufsDecoder.ofFixedSize(4)
            .andThen(buf -> buf.asString(UTF_8));

    private static final String RESPONSE = "PONG FROM ACTIVEJ";

    public static void main(String[] args) {
        new Main();
    }
    public Main() {
        run();
    }
    public void run() {
    try{
            Eventloop eventloop = Eventloop.create().withCurrentThread();
            SimpleServer server = SimpleServer.create(
                            socket -> {
                                BinaryChannelSupplier bufsSupplier = BinaryChannelSupplier.of(ChannelSupplier.ofSocket(socket));
                                Promises.repeat(() ->
                                                bufsSupplier.decode(DECODER)
                                                        .whenResult(x -> {
                                                            ByteBuf buf = ByteBufStrings.wrapAscii(x);
                                                            System.out.println(buf.asString(UTF_8) + "|" + Arrays.toString(x.getBytes(UTF_8)) + x.getBytes(UTF_8).length);
                                                        })
                                                        .then(() -> {
                                                            System.out.println("SENDING DATA TO NETTY !!!!" + Arrays.toString(RESPONSE.getBytes()));
                                                            return socket.write(ByteBufStrings.wrapAscii(RESPONSE));
                                                        })
                                                        .map($ -> true))
                                        .whenComplete(socket::close);
                            })
                    .withListenAddress(ADDR);
        server.listen();
        eventloop.run();
    } catch (IOException e) {
        throw new RuntimeException(e);
        }
    }
}
