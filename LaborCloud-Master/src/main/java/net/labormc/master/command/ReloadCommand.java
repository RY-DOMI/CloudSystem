package net.labormc.master.command;

import net.labormc.cloudapi.command.CloudCommand;
import net.labormc.master.Master;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ReloadCommand extends CloudCommand {

    public ReloadCommand() {
        super("reload", "Reloads the current system");
    }

    @Override
    public boolean execute(String[] args) {
        System.out.println("Reloading the system...");
        if (Master.getInstance().onReload()) {
            System.out.println("Successfully reloaded the system.");
        } else {
            System.err.println("Couldn't reload the system!");
        }
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public List<String> getUsageList() {
        return null;
    }
}
