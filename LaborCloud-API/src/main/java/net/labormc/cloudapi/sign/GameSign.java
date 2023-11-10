package net.labormc.cloudapi.sign;

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
public class GameSign {

    private UUID uniqueId;

    private CloudSign cloudSign;
    private String gameName;
    private SignLocation location;

}
