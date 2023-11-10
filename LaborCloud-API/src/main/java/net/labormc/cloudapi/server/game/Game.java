package net.labormc.cloudapi.server.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.server.game.map.ServerMap;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Game {

    private String id;
    private String name;

    private GameStates state;
    private ServerMap map;
}
