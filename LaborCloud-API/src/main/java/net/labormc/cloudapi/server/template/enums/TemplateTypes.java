package net.labormc.cloudapi.server.template.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
@Getter
public enum TemplateTypes {

    SERVER("Server"),
    PROXY("Proxy");

    private final String displayName;
}
