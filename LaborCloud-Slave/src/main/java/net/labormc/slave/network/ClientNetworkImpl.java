package net.labormc.slave.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.network.client.Client;
import net.labormc.cloudapi.network.client.IClientNetwork;
import net.labormc.cloudapi.network.protocol.codec.PacketDecoder;
import net.labormc.cloudapi.network.protocol.codec.PacketEncoder;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class ClientNetworkImpl implements IClientNetwork {

    private final String hostName;
    private final int port;

    private final Client client;

    @Override
    public void connect() {
        System.out.println("Trying to connect to cloud...");

        final boolean epoll = Epoll.isAvailable();
        final EventLoopGroup eventLoopGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            final InetSocketAddress clientAddress = this.client.getAddress();
            final Bootstrap bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
                    .localAddress(clientAddress.getHostName(), clientAddress.getPort())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.config().setOption(ChannelOption.IP_TOS, 0x18);

                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new PacketDecoder())
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new PacketEncoder())
                                    .addLast(new ClientNetworkHandler());
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = null;
            channelFuture = bootstrap.connect(this.hostName, this.port);

            channelFuture.channel().closeFuture().addListener(future -> {
                System.err.println("Network timeout! Reconnect in 5 seconds...");
                Thread.sleep(5000);

                this.connect();
                eventLoopGroup.shutdownGracefully();
            }).sync();
        } catch (Exception ex) {
            Logger.getLogger(ClientNetworkImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
