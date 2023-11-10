package net.labormc.launcher.command;

import net.labormc.cloudapi.command.CloudCommand;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ShutdownCommand extends CloudCommand {

    public ShutdownCommand() {
        super("shutdown", "Shutdowns the current system");
    }

    @Override
    public boolean execute(String[] args) {
        System.exit(0);
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public List<String> getUsageList() {
        return null;
    }
}
