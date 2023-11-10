package net.labormc.slave.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdateTemplate;
import net.labormc.slave.Slave;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayOutUpdateTemplateListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayOutUpdateTemplate packet, ChannelHandlerContext ctx) {
        final String name = packet.getTemplate().getName();
        System.out.println("Updating Template " + name + "...");

        FileUtils.deleteQuietly(new File("cloud/templates/" + name));

        Slave.getInstance().getTemplateLoader().downloadTemplate(name);
    }
}
