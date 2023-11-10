package net.labormc.cloudapi.server.registry;

import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.template.Template;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IServerRegistry {

    void registerServer(CloudServer server);
    void unregisterServer(CloudServer server);

    <T extends CloudServer> T getServer(String name, Class<T> clazz);

    CloudServer getServer(String name);

    List<CloudServer> getAll(Template template);
    List<CloudServer> getAll();
}
