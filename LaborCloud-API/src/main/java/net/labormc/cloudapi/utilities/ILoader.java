package net.labormc.cloudapi.utilities;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface ILoader<K, V> {

    V load(K k);

    void save(V v);
}
