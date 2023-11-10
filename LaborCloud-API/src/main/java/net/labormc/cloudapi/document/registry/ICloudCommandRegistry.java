package net.labormc.cloudapi.document.registry;

import net.labormc.cloudapi.command.CloudCommand;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface ICloudCommandRegistry extends Runnable {

    ICloudCommandRegistry registerCommand(CloudCommand command);
    ICloudCommandRegistry unregisterCommand(CloudCommand command);

    CloudCommand getCommand(String name);

    void dispatchCommand(String line);

    List<CloudCommand> getAll();

}
