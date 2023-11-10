package net.labormc.cloudapi.resource.registry;

import net.labormc.cloudapi.resource.Resource;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IResourceRegistry {

    void registerResource(Resource resource);
    void unregisterResource(Resource resource);

    Resource getResource(String name);

    List<Resource> getAll();
}
