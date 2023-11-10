package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayOutUpdateResource;
import net.labormc.cloudapi.resource.Resource;
import net.labormc.slave.Slave;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutUpdateResourceListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutUpdateResource packet, ChannelHandlerContext ctx) {
        final String name = Slave.getInstance().getObject().getName();
        final Resource resource = packet.getResource();
        CloudAPI.getInstance().getResourceRegistry().getResource(name).setResource(resource.getCpuAverage(),
                resource.getUsedRam(), resource.getMaxRam());
    }
}
