package net.labormc.cloudapi.network.protocol.registry;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IPacketRegistry {

    IPacketRegistry registerPacket(IPacket packet);
    IPacketRegistry registerListener(IPacketListener listener);
    void unregisterPacket(IPacket packet);
    void unregisterListener(IPacketListener listener);

    IPacket getPacket(String name);
    String getName(IPacket packet);

    void callIncoming(ChannelHandlerContext channel, IPacket packet);
}
