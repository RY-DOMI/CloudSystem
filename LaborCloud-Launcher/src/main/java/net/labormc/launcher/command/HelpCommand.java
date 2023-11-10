package net.labormc.launcher.command;

import com.sun.management.OperatingSystemMXBean;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.command.CloudCommand;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class HelpCommand extends CloudCommand {

    private final OperatingSystemMXBean system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final DecimalFormat format = new DecimalFormat("##.##");

    public HelpCommand() {
        super("help", "Lists all available commands");
        this.system.getProcessCpuLoad();
    }

    @Override
    public boolean execute(String[] args) {
        return false;
    }

    @Override
    public List<String> getUsageList() {
        final List<String> usageList = new LinkedList<>();
        CloudAPI.getInstance().getCloudCommandRegistry().getAll().forEach(command -> {
            usageList.add(command.getName() + " | " + command.getDescription());
        });
        final int maxMemory = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L);
        final int usedMemory = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L);
        usageList.add(" ");
        usageList.add(" Memory: " + usedMemory + " / " + maxMemory);
        usageList.add(" Cpu Average: " + this.format.format(system.getProcessCpuLoad()));
        usageList.add(" ");
        return usageList;
    }
}
