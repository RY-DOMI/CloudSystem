package net.labormc.cloudapi.service;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IService {


    void onInit();
    void onEnable();
    void onDisable();
    boolean onReload();
}
