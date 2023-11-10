package net.labormc.master.command;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.command.CloudCommand;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.template.Template;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerCommand extends CloudCommand {

    public ServerCommand() {
        super("server", "Manage all available Servers");
    }

    private void list() {
        if (CloudAPI.getInstance().getTemplateRegistry().getAll().isEmpty()) {
            System.err.println("Three are no templates registered!");
            return;
        }
        CloudAPI.getInstance().getTemplateRegistry().getAll().forEach((template) -> {
            System.out.println(" ");
            System.out.println(MessageFormat.format("Template [name={0}/type={1}]:", template.getName(), template.getType().name()));

            final List<CloudServer> serverList = CloudAPI.getInstance().getServerRegistry().getAll().stream()
                    .filter(server -> CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName()).getName()
                            .equalsIgnoreCase(template.getName())).toList();

            if (serverList.isEmpty()) {
                System.err.println(" - There are no servers running!");
                return;
            }

            switch (template.getType()) {
                case PROXY:
                    serverList.forEach((server) -> System.out.println(MessageFormat.format(" - Proxy [name={0}/template={1}/onlineCount={2}]",
                            server.getName(), template.getName(), server.getOnlineCount())));
                    break;
                case SERVER:
                    serverList.forEach((server) -> System.out.println(MessageFormat.format(" - Server [name={0}/template={1}/onlineCount={2}]",
                            server.getName(), template.getName(), server.getOnlineCount())));
                    break;
            }
            System.out.println(" ");
        });
        System.out.println(" ");
    }

    private void stop(String[] args) {
        String serverName = args[1];

        if (CloudAPI.getInstance().getServerRegistry().getServer(serverName) == null) {
            System.err.println("There is no server running with the name " + serverName + "!");
            return;
        }
        //Master.getInstance().getServerQueue().stopServer(serverName);
    }

    private void stopAll(String[] args) {
        String templateName = args[1];
        Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(templateName);

        if (template == null) {
            System.err.println("Couldn't find a template with name " + templateName + "!");
            return;
        }
        if (CloudAPI.getInstance().getServerRegistry().getAll(template).isEmpty()) {
            System.err.println("There are no servers running by the template " + templateName + "!");
            return;
        }

        CloudAPI.getInstance().getServerRegistry().getAll(template).forEach((server) -> {
            //Master.getInstance().getServerQueue().stopServer(server.getName());

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void start(String[] args) {
        String templateName = args[1];
        Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(templateName);

        if (template == null) {
            System.err.println("Couldn't find a template with name " + templateName + "!");
            return;
        }
        int amount = Integer.parseInt(args[2]);

        for (int i = 0; i < amount; i++) break;
            //Master.getInstance().getServerQueue().addToQueue(new QueueServer(UUID.randomUUID(), template));
        System.out.println("Added " + amount + " for template " + template.getName() + " to queue!");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            list();
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
            stop(args);
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stopAll")) {
            stopAll(args);
            return true;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            start(args);
            return true;
        }
        return true;
    }

    @Override
    public List<String> getUsageList() {
        final List<String> usageList = new LinkedList<>();

        usageList.add("server list | List all servers");
        usageList.add("server start <Template> <Amount> | Start servers by amount");
        usageList.add("server stop <Name> | Stops a server");
        usageList.add("server stopAll <TemplateName> | Stops a template");
        return usageList;
    }
}
