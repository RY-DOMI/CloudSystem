package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdatePlugin;
import net.labormc.slave.Slave;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutUpdatePluginListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutUpdatePlugin packet, ChannelHandlerContext ctx) {
        final String name = packet.getName();
        System.out.println("Updating Plugin " + name + "...");

        FileUtils.deleteQuietly(new File("cloud/plugins/" + name + ".zip"));
        Slave.getInstance().getTemplateLoader().downloadPlugin(name);
    }
}
