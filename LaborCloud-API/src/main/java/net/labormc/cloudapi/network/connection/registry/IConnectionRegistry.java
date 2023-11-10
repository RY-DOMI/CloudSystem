package net.labormc.cloudapi.network.connection.registry;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.client.Client;
import net.labormc.cloudapi.network.connection.Connection;

import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IConnectionRegistry {

    void registerConnection(Connection connection);
    void unregisterConnection(Connection connection);

    Connection getConnection(UUID connectionUniqueId);
    Connection getConnection(String name);
    Connection getConnection(ChannelHandlerContext channel);

    List<Connection> getAll();
}
