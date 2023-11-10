package net.labormc.cloudapi.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.sign.enums.SignLayoutStates;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */

@AllArgsConstructor
@Getter
@Setter
public class CloudSign {

    private SignLayoutStates state;
    private MinecraftServer server;
}
