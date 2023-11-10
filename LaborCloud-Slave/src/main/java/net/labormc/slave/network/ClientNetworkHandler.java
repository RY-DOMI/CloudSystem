package net.labormc.slave.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.connection.ConnectionTypes;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IPacket packet) throws Exception {
        CloudAPI.getInstance().getPacketRegistry().callIncoming(channelHandlerContext, packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CloudAPI.getInstance().getConnectionRegistry().registerConnection(new Connection(UUID.randomUUID(),
                "CLOUD", ConnectionTypes.MASTER, ctx));
        System.out.println("Connected to Cloud @" + ctx.channel().remoteAddress().toString());

        ctx.writeAndFlush(new PacketPlayInClientConnect(Slave.class, net.labormc.slave.Slave.getInstance().getObject()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}
