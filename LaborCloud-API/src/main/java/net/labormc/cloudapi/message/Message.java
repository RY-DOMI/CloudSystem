package net.labormc.cloudapi.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.user.enums.LanguageTypes;

import java.util.Map;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Message {

    private String name;
    private Map<LanguageTypes, String> messages;

}
