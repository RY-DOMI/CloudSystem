package net.labormc.cloudapi.network.client.types;

import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.game.Game;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
@Setter
public class MinecraftServer extends CloudServer {

    private String extra;
    private Game game;

    public MinecraftServer(UUID uniqueId, String name, String hostName, int port, int onlineCount, String templateName,
                           UUID slaveUniqueId, String extra, Game game) {
        super(uniqueId, name, hostName, port, onlineCount, templateName, slaveUniqueId);
        this.extra = extra;
        this.game = game;
    }
}
