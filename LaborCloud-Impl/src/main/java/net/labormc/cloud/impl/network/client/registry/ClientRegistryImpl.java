package net.labormc.cloud.impl.network.client.registry;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.Client;
import net.labormc.cloudapi.network.client.registry.IClientRegistry;
import net.labormc.cloudapi.network.connection.Connection;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ClientRegistryImpl implements IClientRegistry {

    private final List<Client> clientList = new LinkedList<>();

    @Override
    public void registerClient(Client client) {
        if (!this.clientList.contains(client))
            this.clientList.add(client);
    }

    @Override
    public void unregisterClient(Client client) {
        this.clientList.remove(client);
    }

    @Override
    public Client getClient(UUID clientUniqueId) {
        return this.clientList.stream()
                .filter(client -> Objects.equals(client.getUniqueId(), clientUniqueId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Client getClient(String name) {
        return this.clientList.stream()
                .filter(client -> Objects.equals(client.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Client getClient(ChannelHandlerContext channel) {
        return this.clientList.stream()
                .filter(client -> Objects.equals(CloudAPI.getInstance().getConnectionRegistry().getConnection(channel).getUniqueId(), client.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Client getClient(Connection connection) {
        return this.clientList.stream()
                .filter(client -> Objects.equals(client.getUniqueId(), connection.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Client> getAll(Class<? extends Client> clazz) {
        return this.clientList.stream()
                .filter(client -> client.getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())).toList();
    }

    @Override
    public List<Client> getAll() {
        return this.clientList;
    }
}
