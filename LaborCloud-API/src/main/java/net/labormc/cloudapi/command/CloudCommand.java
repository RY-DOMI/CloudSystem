package net.labormc.cloudapi.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
@Getter
public abstract class CloudCommand {

    public final String name;
    private final String description;

    public abstract boolean execute(String[] args);
    public abstract List<String> getUsageList();

}