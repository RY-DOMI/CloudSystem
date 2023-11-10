package net.labormc.cloud.impl.command.registry;

import jline.console.ConsoleReader;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.command.CloudCommand;
import net.labormc.cloudapi.document.registry.ICloudCommandRegistry;
import net.labormc.cloudapi.logging.ConsoleColors;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudCommandRegistryImpl implements ICloudCommandRegistry {

    private final List<CloudCommand> cloudCommandList = new LinkedList<>();

    @Override
    public void run() {
        final ConsoleReader reader = CloudAPI.getInstance().getCloudLogger().getReader();
        String user = System.getProperty("user.name");

        while (true) {
            try {
                String input;
                while ((input = reader.readLine(ConsoleColors.BLUE_BOLD + user + ConsoleColors.BLACK_BOLD + "@" +
                        ConsoleColors.WHITE_BRIGHT + "Cloud" + ConsoleColors.BLACK_BOLD + ":~$ " + ConsoleColors.RESET)) != null) {
                    reader.setPrompt("");
                    this.dispatchCommand(input);
                }

            } catch (IOException ex) {
                Logger.getLogger(CloudCommandRegistryImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public ICloudCommandRegistry registerCommand(CloudCommand command) {
        if (!this.cloudCommandList.contains(command)) {
            this.cloudCommandList.add(command);
        }
        return this;
    }

    @Override
    public ICloudCommandRegistry unregisterCommand(CloudCommand command) {
        this.cloudCommandList.remove(command);
        return this;
    }

    @Override
    public void dispatchCommand(String line) {
        final String[] split = line.split(" ", -1);
        if (split.length == 0 || split[0].isEmpty()) {
            return;
        }
        final String name = split[0].toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        final CloudCommand command = this.getCommand(name);
        if (command != null) {
            if (!command.execute(args)) {
                System.out.println(" ");
                command.getUsageList().forEach(System.out::println);
                System.out.println(" ");
            }
        } else {
            System.out.println("Couldn't find any command with the name \"" + name + "\"!");
        }
    }

    @Override
    public CloudCommand getCommand(String name) {
        return this.cloudCommandList.stream()
                .filter(command -> command.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CloudCommand> getAll() {
        return this.cloudCommandList;
    }
}
