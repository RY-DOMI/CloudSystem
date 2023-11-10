package net.labormc.cloudapi.network.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
@Getter
public enum ConnectionTypes {

    MASTER("Master"),
    SLAVE("Slave"),
    PROXY_SERVER("Proxy"),
    MINECRAFT_SERVER("Server");

    private final String displayName;
}
