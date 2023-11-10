package net.labormc.cloudapi.server.game.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class ServerMap {

    private String templateName;
    private String name;

    private List<String> builders;
}
