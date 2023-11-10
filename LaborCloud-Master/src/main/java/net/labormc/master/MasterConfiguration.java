package net.labormc.master;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.cloudapi.server.entities.Motd;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.sign.SignLayout;
import net.labormc.cloudapi.sign.SignLocation;
import net.labormc.master.cloudflare.config.CloudFlareConfig;
import net.labormc.master.database.user.web.WebConfiguration;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class MasterConfiguration {

    private NetworkSettings networkSettings;
    private ServerSettings serverSettings;
    private CloudSettings cloudSettings;
    private WebConfiguration webSettings;
    private DatabaseSettings databaseSettings;

    private List<Message> messageList;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class NetworkSettings {

        private String hostName;
        private int port;

        private List<Slave> slaveList;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ServerSettings {

        private boolean maintenance;
        private String maintenanceKickMessage;

        private String header;
        private String footer;

        private List<Motd> motdList;
        private List<String> playerInfoList;
        private List<SignLayout> signLayoutList;
        private List<SignLocation> signLocationList;
        private List<Template> templateList;
        private List<String> whitelist;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CloudSettings {

        private List<String> allowedAddress;
        private List<String> deniedConnectedAddress;
        private List<CloudFlareConfig> cloudFlareList;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class DatabaseSettings {

        private String hostName;
        private int port;

        private String userName;
        private String password;
        private String databaseName;
    }


}
