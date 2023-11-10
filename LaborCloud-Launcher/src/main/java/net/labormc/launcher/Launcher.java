package net.labormc.launcher;

import net.labormc.cloud.impl.CloudAPIImpl;
import net.labormc.cloud.impl.logging.CloudLoggerImpl;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayInAPIRequest;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayOutAPIResponse;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayOutConnectionSuccess;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerConnected;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerDisconnected;
import net.labormc.cloudapi.network.protocol.packets.cloudplayer.PacketPlayInCloudPlayerUpdate;
import net.labormc.cloudapi.network.protocol.packets.maintenance.PacketPlayOutUpdateMaintenance;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayInResourceInfo;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayOutUpdateResource;
import net.labormc.cloudapi.network.protocol.packets.server.*;
import net.labormc.cloudapi.network.protocol.packets.tablist.PacketPlayOutUpdateTablist;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdatePlugin;
import net.labormc.cloudapi.network.protocol.packets.template.PacketPlayOutUpdateTemplate;
import net.labormc.cloudapi.service.IService;
import net.labormc.launcher.command.HelpCommand;
import net.labormc.launcher.command.ShutdownCommand;
import net.labormc.master.Master;
import net.labormc.slave.Slave;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class Launcher {

    public static void main(String[] args) {
        final StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("  _          _                 ____ _                 _\n");
        headerBuilder.append(" | |    __ _| |__   ___  _ __ / ___| | ___  _   _  __| |\n");
        headerBuilder.append(" | |   / _` | '_ \\ / _ \\| '__| |   | |/ _ \\| | | |/ _` |\n");
        headerBuilder.append(" | |__| (_| | |_) | (_) | |  | |___| | (_) | |_| | (_| |\n");
        headerBuilder.append(" |_____\\__,_|_.__/ \\___/|_|   \\____|_|\\___/ \\__,_|\\__,_|\n\n");

        final String serviceArg = args[0];
        if (!serviceArg.contains("-service="))
            return;

        final String serviceName = serviceArg.split("=")[1].toUpperCase();

        headerBuilder.append("\t~ Service: ").append(serviceName).append("\n");
        headerBuilder.append("\t~ This software was developed by Ryixz (Dominik Auer)\n\n");
        System.out.println(headerBuilder);

        final CloudAPIImpl cloudAPI = new CloudAPIImpl();
        try {
            cloudAPI.setCloudLogger(new CloudLoggerImpl());
            new Thread(cloudAPI.getCloudCommandRegistry(), "command-Thread").start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CloudAPI.setInstance(cloudAPI);

        registerPackets();
        registerCommands();
        startService(serviceName);
    }

    private static void startService(String serviceName) {
        IService service = null;
        switch (serviceName) {
            case "MASTER":
                service = new Master();
                break;

            case "SLAVE":
                service = new Slave();
                break;

            default:
                System.err.println("Service (name=" + serviceName + ") failed to load! Reason: The specified service is not available, use MASTER or SLAVE!");
                Runtime.getRuntime().exit(0);
                break;
        }

        try {
            service.onInit();
            service.onEnable();
            Runtime.getRuntime().addShutdownHook(shutdownHook(service));
        } catch (Exception ex) {
            System.err.println("Service (name=" + serviceName + ") failed to load or enable!");
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Thread shutdownHook(IService service) {
        return new Thread(() -> {
            service.onDisable();
            System.out.println("Service (name=" + service.getClass().getSimpleName() + ") disabled!");

            CloudAPI.getInstance().getCloudLogger().shutdown();
        });
    }

    private static void registerPackets() {
        CloudAPI.getInstance().getPacketRegistry()
                .registerPacket(new PacketPlayInClientConnect())
                .registerPacket(new PacketPlayOutConnectionSuccess())
                .registerPacket(new PacketPlayInResourceInfo())
                .registerPacket(new PacketPlayOutUpdateResource())
                .registerPacket(new PacketPlayOutStartServer())
                .registerPacket(new PacketPlayOutStopServer())
                .registerPacket(new PacketPlayOutUpdateTemplate())
                .registerPacket(new PacketPlayOutUpdatePlugin())
                .registerPacket(new PacketPlayOutUpdatePlugin())
                .registerPacket(new PacketPlayOutRegisterServer())
                .registerPacket(new PacketPlayOutUnregisterServer())
                .registerPacket(new PacketPlayInUpdateServerInfo())
                .registerPacket(new PacketPlayInStartServer())
                .registerPacket(new PacketPlayInRequestServer())
                .registerPacket(new PacketPlayInDeleteServer())
                .registerPacket(new PacketPlayInAPIRequest())
                .registerPacket(new PacketPlayOutAPIResponse())
                .registerPacket(new PacketPlayOutUpdateMaintenance())
                .registerPacket(new PacketPlayInCloudPlayerConnected())
                .registerPacket(new PacketPlayInCloudPlayerDisconnected())
                .registerPacket(new PacketPlayInCloudPlayerUpdate())
                .registerPacket(new PacketPlayOutUpdateTablist())
                .registerPacket(new PacketPlayOutStartProxy())
                .registerPacket(new PacketPlayOutUpdateSign());
    }

    private static void registerCommands() {
        CloudAPI.getInstance().getCloudCommandRegistry()
                .registerCommand(new HelpCommand())
                .registerCommand(new ShutdownCommand());
    }
}
