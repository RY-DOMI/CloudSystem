package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInDeleteServer;
import net.labormc.master.Master;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInDeleteServerListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInDeleteServer packet, ChannelHandlerContext ctx) {
        final String name = packet.getName();
        if (CloudAPI.getInstance().getServerRegistry().getServer(name) == null) {
            System.err.println("There is no server running with the name " + name + "!");
            return;
        }
        Master.getInstance().getServerQueue().stopServer(name);

        CloudAPI.getInstance().getServerRegistry().getAll().forEach(server -> {
            CloudAPI.getInstance().getConnectionRegistry().getConnection(server.getName())
                    .getChannel().writeAndFlush(packet);
        });
    }

}
