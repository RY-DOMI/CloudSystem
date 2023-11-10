package net.labormc.master.cloudflare;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.master.cloudflare.config.CloudFlareConfig;
import net.labormc.master.cloudflare.record.DnsRecord;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class PostResponse {

    private CloudFlareConfig cloudFlareConfig;
    private DnsRecord dnsRecord;

    private String id;
}
