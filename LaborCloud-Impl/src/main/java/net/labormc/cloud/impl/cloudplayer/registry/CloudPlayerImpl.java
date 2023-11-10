package net.labormc.cloud.impl.cloudplayer.registry;

import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.cloudplayer.registry.ICloudPlayerRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudPlayerImpl implements ICloudPlayerRegistry {

    private final List<CloudPlayer> cloudPlayerList = new LinkedList<>();

    @Override
    public void registerCloudPlayer(CloudPlayer cloudPlayer) {
        if (!this.cloudPlayerList.contains(cloudPlayer))
            this.cloudPlayerList.add(cloudPlayer);
    }

    @Override
    public void unregisterCloudPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayerList.remove(cloudPlayer);
    }

    @Override
    public CloudPlayer getCloudPlayer(UUID uuid) {
        return this.cloudPlayerList.stream()
                .filter(cloudPlayer -> Objects.equals(cloudPlayer.getUuid(), uuid))
                .findFirst()
                .orElse(null);
    }

    @Override
    public CloudPlayer getCloudPlayer(String name) {
        return this.cloudPlayerList.stream()
                .filter(cloudPlayer -> Objects.equals(cloudPlayer.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CloudPlayer> getAll() {
        return this.cloudPlayerList;
    }
}
