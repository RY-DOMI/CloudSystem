package net.labormc.cloudapi.network.client;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IClientNetwork extends Runnable {

    @Override
    default void run() {
        this.connect();
    }

    void connect();
}
