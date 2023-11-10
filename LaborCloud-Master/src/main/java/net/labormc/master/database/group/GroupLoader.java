package net.labormc.master.database.group;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mongodb.client.model.Filters;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.group.Group;
import net.labormc.cloudapi.utilities.ILoader;
import net.labormc.master.database.DatabaseManager;
import org.bson.Document;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class GroupLoader implements ILoader<String, Group> {

    private final DatabaseManager databaseManager;
    private final Cache<String, Group> cache;

    public GroupLoader(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Group load(String name) {
                        return this.load(name);
                    }
                });
    }

    @Override
    public Group load(String name) {
        final ConcurrentMap<String, Group> map = this.cache.asMap();
        if ((map.containsKey(name)) && (map.get(name) != null))
            return map.get(name);

        final Group group = CloudAPI.getInstance().getGson().fromJson(Objects.requireNonNull(
                this.databaseManager.getUsers().find(Filters.eq("name", name))
                        .first()).toJson(), Group.class);
        if (group != null)
            this.cache.put(name, group);

        return group;
    }

    @Override
    public void save(Group group) {
        final String name = group.getName();
        final Document document = Document.parse(CloudAPI.getInstance().getGson().toJson(group));

        if (this.load(name) == null)
            this.databaseManager.getUsers().insertOne(document);
        else
            this.databaseManager.getUsers().replaceOne(Filters.eq("name", name), document);
    }
}
