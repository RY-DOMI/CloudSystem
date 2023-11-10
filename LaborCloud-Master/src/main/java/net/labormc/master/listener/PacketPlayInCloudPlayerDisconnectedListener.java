package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerDisconnected;

import java.text.MessageFormat;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInCloudPlayerDisconnectedListener implements IPacketListener {
    
    @IPacketHandler
    public void on(PacketPlayInCloudPlayerDisconnected packet, ChannelHandlerContext ctx) {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerRegistry().getCloudPlayer(packet.getCloudPlayerUUID());
        CloudAPI.getInstance().getCloudPlayerRegistry().unregisterCloudPlayer(cloudPlayer);

        System.out.println(MessageFormat.format("CloudPlayer [uuid={0}/name={1}] disconnected!", cloudPlayer.getUuid().toString(),
                cloudPlayer.getName()));
    }

}
