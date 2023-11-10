package net.labormc.master.cloudflare.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class DnsRecordId {

    private String id;
    private String type;
    private String name;
    private String content;

    private int ttl;

    private boolean proxied;
    private JSONObject data;
}
