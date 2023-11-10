package net.labormc.master.cloudflare.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class CloudFlareConfig {

    private String base_path;

    private String email;
    private String authKey;
    private String domainName;
    private String zoneId;
}
