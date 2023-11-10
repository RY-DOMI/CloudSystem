package net.labormc.cloudapi.network.client.types;

import net.labormc.cloudapi.network.client.Client;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class Slave extends Client {


    public Slave(UUID uniqueId, String name, String hostName, int port) {
        super(uniqueId, name, hostName, port);
    }
}
