package net.labormc.cloud.impl.network.connection.registry;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.client.Client;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.connection.registry.IConnectionRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ConnectionRegistryImpl implements IConnectionRegistry {

    private final List<Connection> connectionList = new LinkedList<>();

    @Override
    public void registerConnection(Connection connection) {
        if (!this.connectionList.contains(connection))
            this.connectionList.add(connection);
    }

    @Override
    public void unregisterConnection(Connection connection) {
        this.connectionList.remove(connection);
    }

    @Override
    public Connection getConnection(UUID connectionUniqueId) {
        return this.connectionList.stream()
                .filter(connection -> Objects.equals(connection.getUniqueId(), connectionUniqueId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Connection getConnection(String name) {
        return this.connectionList.stream()
                .filter(connection -> Objects.equals(connection.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Connection getConnection(ChannelHandlerContext channel) {
        return this.connectionList.stream()
                .filter(connection -> Objects.equals(connection.getChannel(), channel))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Connection> getAll() {
        return this.connectionList;
    }
}
