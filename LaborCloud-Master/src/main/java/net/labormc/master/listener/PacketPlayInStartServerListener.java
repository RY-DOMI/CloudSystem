package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInStartServer;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInStartServerListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInStartServer packet, ChannelHandlerContext ctx) {

        CloudAPI.getInstance().getServerRegistry().getAll().forEach((server) -> {
            Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection(server.getName());

            if (connection == null)
                return;
            connection.getChannel().writeAndFlush(packet);
        });
    }

}
