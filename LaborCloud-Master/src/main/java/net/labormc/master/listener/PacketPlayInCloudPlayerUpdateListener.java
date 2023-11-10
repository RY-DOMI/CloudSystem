package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerUpdate;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInCloudPlayerUpdateListener implements IPacketListener {
    
    @IPacketHandler
    public void on(PacketPlayInCloudPlayerUpdate packet, ChannelHandlerContext ctx) {
        final CloudPlayer cloudPlayer = packet.getCloudPlayer();
        CloudAPI.getInstance().getCloudPlayerRegistry().getAll().set(
                CloudAPI.getInstance().getCloudPlayerRegistry().getAll().indexOf(
                        CloudAPI.getInstance().getCloudPlayerRegistry().getCloudPlayer(cloudPlayer.getUuid())), cloudPlayer);
    }

}
