package net.labormc.cloudapi.network.client.registry;

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
public interface IClientRegistry {

    void registerClient(Client client);
    void unregisterClient(Client client);

    Client getClient(UUID clientUniqueId);
    Client getClient(String name);
    Client getClient(ChannelHandlerContext channel);
    Client getClient(Connection connection);

    List<Client> getAll(Class<? extends Client> clazz);
    List<Client> getAll();
}
