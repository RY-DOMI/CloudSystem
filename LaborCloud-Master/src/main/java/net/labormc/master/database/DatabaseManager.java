package net.labormc.master.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.labormc.master.MasterConfiguration;
import org.bson.Document;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class DatabaseManager {

    private MasterConfiguration.DatabaseSettings settings;

    private MongoClient client;
    private MongoDatabase database;

    @Getter
    private MongoCollection<Document> users;
    @Getter
    private MongoCollection<Document> groups;

    public DatabaseManager(MasterConfiguration.DatabaseSettings settings) {
        this.settings = settings;
    }

    public void connect() {
        MongoCredential credential = MongoCredential.createCredential(this.settings.getUserName(), this.settings.getDatabaseName(),
                this.settings.getPassword().toCharArray());

        this.client = new MongoClient(new ServerAddress(this.settings.getHostName(), this.settings.getPort()),
                credential, new MongoClientOptions.Builder().build());
        this.database = this.client.getDatabase(this.settings.getDatabaseName());

        this.users = this.database.getCollection("users");
        this.groups = this.database.getCollection("groups");
    }
}
