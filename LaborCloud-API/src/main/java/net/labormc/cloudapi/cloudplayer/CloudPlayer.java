package net.labormc.cloudapi.cloudplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class CloudPlayer {

    private UUID uuid;

    private String name;

    private UUID serverUniqueId;
    private UUID proxyUniqueId;
}
