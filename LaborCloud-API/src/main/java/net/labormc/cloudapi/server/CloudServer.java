package net.labormc.cloudapi.server;

import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.network.client.Client;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
@Setter
public class CloudServer extends Client {

    private int onlineCount;

    private String templateName;
    private UUID slaveUniqueId;

    public CloudServer(UUID uniqueId, String name, String hostName, int port, int onlineCount, String templateName, UUID slaveUniqueId) {
        super(uniqueId, name, hostName, port);
        this.onlineCount = onlineCount;
        this.templateName = templateName;
        this.slaveUniqueId = slaveUniqueId;
    }
}
