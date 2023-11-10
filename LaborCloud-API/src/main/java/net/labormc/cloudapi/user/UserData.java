package net.labormc.cloudapi.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.user.enums.LanguageTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class UserData {

    private UUID uuid;

    private String name;
    private String lastAddress;
    private String groupName;

    private int points;

    private LocalDateTime createdAt;
    private LocalDateTime lastConnectedAt;

    private LanguageTypes language;


}
