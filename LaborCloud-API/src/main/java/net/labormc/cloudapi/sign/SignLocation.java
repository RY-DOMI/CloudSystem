package net.labormc.cloudapi.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class SignLocation {

    private String gameName;
    private String world;

    private double x;
    private double y;
    private double z;

    public static SignLocation deserialize(Map<String, Object> args) {
        return new SignLocation((String) args.get("gameName"), (String) args.get("world"), (double) args.get("x"),
                (double) args.get("y"), (double) args.get("z"));
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("gameName", this.gameName);
        map.put("world", this.world);
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);

        return map;
    }
}
