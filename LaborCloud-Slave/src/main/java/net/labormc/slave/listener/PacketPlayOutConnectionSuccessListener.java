package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayOutConnectionSuccess;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutConnectionSuccessListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutConnectionSuccess packet, ChannelHandlerContext ctx) {
        System.out.println("Connection to master established!");
    }
}
