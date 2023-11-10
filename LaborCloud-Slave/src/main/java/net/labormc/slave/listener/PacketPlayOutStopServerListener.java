package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutStopServer;
import net.labormc.slave.Slave;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutStopServerListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutStopServer packet, ChannelHandlerContext ctx) {
        Slave.getInstance().getServerWorker().stopServer(packet.getName(), packet.getTemplate());
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(PacketPlayOutStopServerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
