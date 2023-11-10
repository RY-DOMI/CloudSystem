package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutStartProxy;
import net.labormc.slave.Slave;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutStartProxyListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutStartProxy packet, ChannelHandlerContext ctx) {
        CloudAPI.getInstance().getTemplateRegistry().registerTemplate(packet.getTemplate());
        Slave.getInstance().getServerWorker().startProxy(packet.getName(), packet.getUniqueId(), packet.getTemplate(),
                packet.getPort());
    }
}
