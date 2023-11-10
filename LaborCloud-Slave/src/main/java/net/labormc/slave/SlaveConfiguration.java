package net.labormc.slave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.slave.web.WebConfiguration;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SlaveConfiguration {

    private NetworkConfig networkSettings;
    private WebConfiguration webSettings;

    private int maxRamSizeToUse;

    private Slave slave;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class NetworkConfig {

        private String hostName;
        private int port;
    }

}
