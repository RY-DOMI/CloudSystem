package net.labormc.cloudapi.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Resource {

    private String name;

    private double cpuAverage;

    private int usedRam;
    private int maxRam;

    public void setResource(double cpuAverage, int usedMemory, int maxMemory) {
        this.cpuAverage = cpuAverage;
        this.usedRam = usedMemory;
        this.maxRam = maxMemory;
    }
}
