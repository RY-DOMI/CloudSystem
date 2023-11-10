package net.labormc.master.database.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mongodb.client.model.Filters;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.user.UserData;
import net.labormc.cloudapi.utilities.ILoader;
import net.labormc.master.database.DatabaseManager;
import org.bson.Document;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class UserLoader implements ILoader<UUID, UserData> {

    private final DatabaseManager databaseManager;
    private final Cache<UUID, UserData> cache;

    public UserLoader(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public UserData load(UUID uniqueId) {
                        return this.load(uniqueId);
                    }
                });
    }

    @Override
    public UserData load(UUID uuid) {
        final ConcurrentMap<UUID, UserData> map = this.cache.asMap();
        if ((map.containsKey(uuid)) && (map.get(uuid) != null))
            return map.get(uuid);

        final UserData userData = CloudAPI.getInstance().getGson().fromJson(Objects.requireNonNull(
                this.databaseManager.getUsers().find(Filters.eq("uuid", uuid))
                        .first()).toJson(), UserData.class);
        if (userData != null)
            this.cache.put(uuid, userData);

        return userData;
    }

    @Override
    public void save(UserData userData) {
        final UUID uuid = userData.getUuid();
        final Document document = Document.parse(CloudAPI.getInstance().getGson().toJson(userData));

        if (this.load(uuid) == null)
            this.databaseManager.getUsers().insertOne(document);
        else
            this.databaseManager.getUsers().replaceOne(Filters.eq("uuid", uuid), document);
    }
}
