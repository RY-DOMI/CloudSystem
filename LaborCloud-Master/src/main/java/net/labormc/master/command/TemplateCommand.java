package net.labormc.master.command;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.command.CloudCommand;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdateModule;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdatePlugin;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdateTemplate;
import net.labormc.cloudapi.server.template.Template;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class TemplateCommand extends CloudCommand {

    public TemplateCommand() {
        super("template", "Manage all available Templates");
    }

    @Override
    public boolean execute(String[] args) {
        switch (args.length) {

            case 2:
                if (args[0].equalsIgnoreCase("update")) {
                    final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(args[1]);

                    if (template == null)
                        System.err.println("Couldn't update Template [name=" + args[1].toUpperCase() + "]!");
                    else {
                        CloudAPI.getInstance().getClientRegistry().getAll(Slave.class).forEach(slave ->
                                CloudAPI.getInstance().getConnectionRegistry().getConnection(slave.getUniqueId()).getChannel()
                                        .writeAndFlush(new PacketPlayOutUpdateTemplate(template)));
                        System.out.println("Template [name=" + template.getName() + "] updated!");
                    }
                } else if (args[0].equalsIgnoreCase("updatePlugin")) {
                    final String name = args[1];

                    CloudAPI.getInstance().getClientRegistry().getAll(Slave.class).forEach(slave ->
                            CloudAPI.getInstance().getConnectionRegistry().getConnection(slave.getName()).getChannel()
                                    .writeAndFlush(new PacketPlayOutUpdatePlugin(name)));
                    System.out.println("Plugin [name=" + name + "] updated!");
                } else if (args[0].equalsIgnoreCase("updateModule")) {
                    final String name = args[1];

                    CloudAPI.getInstance().getClientRegistry().getAll(Slave.class).forEach(slave ->
                            CloudAPI.getInstance().getConnectionRegistry().getConnection(slave.getName()).getChannel()
                                    .writeAndFlush(new PacketPlayOutUpdateModule(name)));

                    System.out.println("Module [name=" + name + "] updated!");
                }
                break;

            default:
                return false;

        }

        return true;
    }

    @Override
    public List<String> getUsageList() {
        final List<String> usageList = new LinkedList<>();

        usageList.add("template update <Name>");
        usageList.add("template updatePlugin <Name>");
        return usageList;
    }
}
