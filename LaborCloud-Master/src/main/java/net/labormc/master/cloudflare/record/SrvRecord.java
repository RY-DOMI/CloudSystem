package net.labormc.master.cloudflare.record;

import lombok.Getter;
import net.labormc.cloudapi.CloudAPI;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class SrvRecord extends DnsRecord {

    public SrvRecord(String name, String content, String service, String proto, String name_, int priority,
            int weight, int port, String target) {
        super(DnsRecordType.SRV.name(), name, content, 1, false, CloudAPI.getInstance().document()
                .append("service", service)
                .append("proto", proto)
                .append("name", name_)
                .append("priority", priority)
                .append("weight", weight)
                .append("port", port)
                .append("target", target).json());
    }

}
