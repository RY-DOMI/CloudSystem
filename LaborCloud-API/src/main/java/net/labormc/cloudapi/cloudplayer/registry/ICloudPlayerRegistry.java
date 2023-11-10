package net.labormc.cloudapi.cloudplayer.registry;

import net.labormc.cloudapi.cloudplayer.CloudPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface ICloudPlayerRegistry {

    void registerCloudPlayer(CloudPlayer cloudPlayer);
    void unregisterCloudPlayer(CloudPlayer cloudPlayer);

    CloudPlayer getCloudPlayer(UUID uuid);
    CloudPlayer getCloudPlayer(String name);

    List<CloudPlayer> getAll();
}
