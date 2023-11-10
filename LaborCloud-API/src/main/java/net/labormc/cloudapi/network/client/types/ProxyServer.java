package net.labormc.cloudapi.network.client.types;

import net.labormc.cloudapi.server.CloudServer;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ProxyServer extends CloudServer {

    public ProxyServer(UUID uniqueId, String name, String hostName, int port, int onlineCount, String templateName,
                       UUID slaveUniqueId) {
        super(uniqueId, name, hostName, port, onlineCount, templateName, slaveUniqueId);
    }
}
