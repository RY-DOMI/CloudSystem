package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayInResourceInfo;
import net.labormc.cloudapi.resource.Resource;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInResourceInfoListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInResourceInfo packet, ChannelHandlerContext ctx) {
        final Resource resource = CloudAPI.getInstance().getResourceRegistry().getResource(packet.getResource().getName());
        if (resource == null) {
            CloudAPI.getInstance().getResourceRegistry().registerResource(packet.getResource());
        } else {
            CloudAPI.getInstance().getResourceRegistry().getResource(resource.getName()).setResource(resource.getCpuAverage(),
                    resource.getUsedRam(), resource.getMaxRam());
        }
    }
}
