package net.labormc.master.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labormc.cloudapi.server.template.Template;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
public class QueueServer {

    private UUID uniqueId;
    private Template template;
}
