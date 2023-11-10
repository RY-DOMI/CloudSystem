package net.labormc.cloudapi.server.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.server.template.enums.TemplateStates;
import net.labormc.cloudapi.server.template.enums.TemplateTypes;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Template {

    private String name;
    private String minGroup;

    private int memory;
    private int maxMemory;
    private int maxPlayers;
    private int spectatingPlayers;
    private int minOnline;

    private boolean maintenance;
    private boolean startNewOnlineCountHalfFull;
    private boolean blockPort;

    private List<String> pluginList;
    private List<String> moduleList;

    private TemplateStates state;
    private TemplateTypes type;

}
