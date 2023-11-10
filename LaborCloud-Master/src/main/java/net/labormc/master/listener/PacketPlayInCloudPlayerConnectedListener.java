package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerConnected;

import java.text.MessageFormat;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInCloudPlayerConnectedListener implements IPacketListener {
    
    @IPacketHandler
    public void on(PacketPlayInCloudPlayerConnected packet, ChannelHandlerContext ctx) {
        final CloudPlayer cloudPlayer = packet.getCloudPlayer();
        CloudAPI.getInstance().getCloudPlayerRegistry().registerCloudPlayer(cloudPlayer);
        
        System.out.println(MessageFormat.format("CloudPlayer [uuid={0}/name={1}] connected!", cloudPlayer.getUuid().toString(),
                cloudPlayer.getName()));
    }

}
