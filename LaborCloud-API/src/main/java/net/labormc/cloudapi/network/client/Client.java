package net.labormc.cloudapi.network.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Client {

    private UUID uniqueId;
    private String name;

    private String hostName;
    private int port;

    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.hostName, this.port);
    }

}
