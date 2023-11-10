package net.labormc.cloud.impl.document;

import com.google.common.io.Files;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class Document extends AbstractDocument {

    private final JSONParser parser = new JSONParser();
    private final JSONObject object;

    public Document() {
        this.object = new JSONObject();
    }

    public Document(JSONObject jsonObject) {
        this.object = jsonObject;
    }

    @Override
    public AbstractDocument append(String key, JSONObject value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(String key, String value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(String key, Number value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(String key, Boolean value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(String key, Object value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(String key, List value) {
        this.object.put(key, value);
        return this;
    }

    @Override
    public AbstractDocument append(Map<String, Object> values) {
        values.forEach(this::append);
        return this;
    }

    @Override
    public Object get(String key) {
        return this.object.get(key);
    }

    @Override
    public String getString(String key) {
        return (String) this.object.get(key);
    }

    @Override
    public long getLong(String key) {
        return (long) this.object.get(key);
    }

    @Override
    public int getInt(String key) {
        return (int) this.object.get(key);
    }

    @Override
    public double getDouble(String key) {
        return (double) this.object.get(key);
    }

    @Override
    public float getFloat(String key) {
        return (float) this.object.get(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return (boolean) this.object.get(key);
    }

    @Override
    public List getList(String key) {
        return (List) this.object.get(key);
    }

    @Override
    public Map getMap(String key) {
        return (Map) this.object.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return CloudAPI.getInstance().getGson().fromJson(CloudAPI.getInstance().getGson().toJson(this.get(key)), clazz);
    }

    @Override
    public AbstractDocument remove(String key) {
        this.object.remove(key);
        return this;
    }

    @Override
    public JSONObject json() {
        return this.object;
    }

    @Override
    public boolean save(File file) {
        try {
            final Path path = file.toPath();
            Files.createParentDirs(file);
            if (java.nio.file.Files.exists(path))
                java.nio.file.Files.createFile(path);

            try (OutputStreamWriter writer = new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
                CloudAPI.getInstance().getGson().toJson(this.object, (writer));
                return true;
            } catch (IOException ex) {
                return false;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public AbstractDocument load(File file) {
        try {
            JSONObject json = (JSONObject) parser.parse(new InputStreamReader(java.nio.file.Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
            return new Document(json);
        } catch (Exception ignored) { }
        return new Document();
    }
}
