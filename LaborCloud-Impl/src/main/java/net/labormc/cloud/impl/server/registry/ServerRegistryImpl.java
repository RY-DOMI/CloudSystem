package net.labormc.cloud.impl.server.registry;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.registry.IServerRegistry;
import net.labormc.cloudapi.server.template.Template;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerRegistryImpl implements IServerRegistry {

    private final List<CloudServer> serverList = new LinkedList<>();

    @Override
    public void registerServer(CloudServer server) {
        if (!this.serverList.contains(server))
            this.serverList.add(server);
    }

    @Override
    public void unregisterServer(CloudServer server) {
        this.serverList.remove(server);
    }

    @Override
    public <T extends CloudServer> T getServer(String name, Class<T> clazz) {
        return (T) this.serverList.stream()
                .filter(server -> (server.getName().equalsIgnoreCase(name) && server.getClass().getSimpleName()
                        .equalsIgnoreCase(clazz.getSimpleName())))
                .findFirst()
                .orElse(null);
    }

    @Override
    public CloudServer getServer(String name) {
        return this.serverList.stream()
                .filter(server -> (server.getName().equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CloudServer> getAll(Template template) {
        return this.serverList.stream()
                .filter(server -> CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName()) == template)
                .collect(Collectors.toList());
    }

    @Override
    public List<CloudServer> getAll() {
        return this.serverList;
    }
}
