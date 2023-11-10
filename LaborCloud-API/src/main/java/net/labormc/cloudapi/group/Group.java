package net.labormc.cloudapi.group;

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
public class Group {

    private String name;
    private String displayName;
    private String prefix;
    private String chatPrefix;

    private List<String> permissions;
}
