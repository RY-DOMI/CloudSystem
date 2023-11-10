package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInRequestServer;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.master.Master;
import net.labormc.master.server.QueueServer;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInRequestServerListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInRequestServer packet, ChannelHandlerContext ctx) {
        final String templateName = packet.getName();
        final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(templateName);
        if (template == null)
            System.err.println("Couldn't find a template with name " + templateName + "!");
        else {
            final int amount = packet.getAmount();
            for (int i = 0; i < amount; i++)
                Master.getInstance().getServerQueue().addToQueue(new QueueServer(UUID.randomUUID(), template));
            System.out.println("Added " + amount + " for template " + template.getName() + " to queue!");
        }
    }

}
