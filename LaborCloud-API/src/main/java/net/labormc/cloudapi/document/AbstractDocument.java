package net.labormc.cloudapi.document;

import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public abstract class AbstractDocument {

    public abstract AbstractDocument append(String key, JSONObject value);
    public abstract AbstractDocument append(String key, String value);
    public abstract AbstractDocument append(String key, Number value);
    public abstract AbstractDocument append(String key, Boolean value);
    public abstract AbstractDocument append(String key, Object value);
    public abstract AbstractDocument append(String key, List value);
    public abstract AbstractDocument append(Map<String, Object> values);

    public abstract Object get(String key);
    public abstract String getString(String key);
    public abstract long getLong(String key);
    public abstract int getInt(String key);
    public abstract double getDouble(String key);
    public abstract float getFloat(String key);
    public abstract boolean getBoolean(String key);
    public abstract List getList(String key);
    public abstract Map getMap(String key);
    public abstract <T> T get(String key, Class<T> clazz);

    public abstract AbstractDocument remove(String key);

    public abstract JSONObject json();

    public abstract boolean save(File file);
    public abstract AbstractDocument load(File file);

    @Override
    public String toString() {
        return json().toJSONString();
    }
}
