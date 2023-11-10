package net.labormc.cloud.impl.resource.registry;

import net.labormc.cloudapi.resource.Resource;
import net.labormc.cloudapi.resource.registry.IResourceRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ResourceRegistryImpl implements IResourceRegistry {

    private final List<Resource> resourceList = new LinkedList<>();

    @Override
    public void registerResource(Resource resource) {
        if (!this.resourceList.contains(resource))
            this.resourceList.add(resource);
    }

    @Override
    public void unregisterResource(Resource resource) {
        this.resourceList.remove(resource);
    }

    @Override
    public Resource getResource(String name) {
        return this.resourceList.stream()
                .filter(resource -> Objects.equals(resource.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Resource> getAll() {
        return this.resourceList;
    }
}
