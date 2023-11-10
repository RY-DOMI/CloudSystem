package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInUpdateServerInfo;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInUpdateServerInfoListener implements IPacketListener {
    
    @IPacketHandler
    public void on(PacketPlayInUpdateServerInfo packet, ChannelHandlerContext ctx) {
        if (packet.getClazz() == ProxyServer.class) {
            final ProxyServer proxy = (ProxyServer) packet.getObject();
            CloudAPI.getInstance().getServerRegistry().getServer(proxy.getName()).setOnlineCount(proxy.getOnlineCount());
        } else if (packet.getClazz() == MinecraftServer.class) {
            final MinecraftServer server = (MinecraftServer) packet.getObject();
            CloudAPI.getInstance().getServerRegistry().getServer(server.getName())
                    .setOnlineCount(server.getOnlineCount());
            ((MinecraftServer) CloudAPI.getInstance().getServerRegistry().getServer(server.getName()))
                    .setExtra(server.getExtra());
            ((MinecraftServer) CloudAPI.getInstance().getServerRegistry().getServer(server.getName()))
                    .setGame(server.getGame());
        }
        CloudAPI.getInstance().getServerRegistry().getAll().forEach(server -> {
            System.out.println(server.getName());
            CloudAPI.getInstance().getConnectionRegistry().getConnection(server.getName())
                    .getChannel().writeAndFlush(packet);
        });
    }

}
