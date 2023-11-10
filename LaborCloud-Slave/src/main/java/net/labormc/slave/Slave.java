package net.labormc.slave;

import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayInResourceInfo;
import net.labormc.cloudapi.resource.Resource;
import net.labormc.cloudapi.service.IService;
import net.labormc.slave.listener.PacketPlayOutConnectionSuccessListener;
import net.labormc.slave.listener.PacketPlayOutUpdateResourceListener;
import net.labormc.slave.network.ClientNetworkImpl;
import net.labormc.slave.server.ServerWorker;
import net.labormc.slave.template.TemplateLoader;
import net.labormc.slave.utils.PortBlocker;
import net.labormc.slave.web.WebConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class Slave implements IService {

    @Getter
    private static Slave instance;

    private OperatingSystemMXBean system;

    private SlaveConfiguration configuration;
    private net.labormc.cloudapi.network.client.types.Slave object;


    private PortBlocker portBlocker;
    private TemplateLoader templateLoader;
    private ServerWorker serverWorker;

    @Override
    public void onInit() {
        instance = this;

        this.system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.portBlocker = new PortBlocker();
        this.serverWorker = new ServerWorker();
        this.loadConfiguration();
    }

    @Override
    public void onEnable() {
        this.registerListeners();
        this.connectToMaster();
        this.runResourceUpdater();

        this.templateLoader = new TemplateLoader();
        this.templateLoader.downloadTemplates();
        this.templateLoader.downloadPlugins();
        this.templateLoader.downloadModules();
    }

    @Override
    public void onDisable() {
        this.serverWorker.getProcessMap().keySet().forEach(key -> this.serverWorker.stopServer(key,
                CloudAPI.getInstance().getTemplateRegistry().getTemplate(key.split("-")[0])));

        try {
            FileUtils.deleteDirectory(new File("templates"));
            FileUtils.deleteDirectory(new File("plugins"));
            FileUtils.deleteDirectory(new File("modules"));
            FileUtils.deleteDirectory(new File("servers"));
        } catch (Exception ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean onReload() {
        return true;
    }

    private void loadConfiguration() {
        try {
            final File configFile = new File("config.json");
            if (!configFile.exists()) {

                CloudAPI.getInstance().document().append("config", new SlaveConfiguration(
                        new SlaveConfiguration.NetworkConfig(
                                "127.0.0.1",
                                6734),
                        new WebConfiguration(
                                "127.0.0.1",
                                4999,
                                "secret_token",
                                "/cloud/v1"
                        ),
                        4048,
                        new net.labormc.cloudapi.network.client.types.Slave(UUID.randomUUID(), "Slave-1",
                                Inet4Address.getLocalHost().getHostAddress(),
                                ThreadLocalRandom.current().nextInt(30000, 32000))))
                        .save(configFile);
            }
            this.configuration = CloudAPI.getInstance().document().load(configFile).get("config", SlaveConfiguration.class);
            this.object = this.configuration.getSlave();

            final Path templatesPath = Paths.get("templates/");
            if (!Files.exists(templatesPath))
                Files.createDirectory(templatesPath);

            final Path pluginsPath = Paths.get("plugins/");
            if (!Files.exists(pluginsPath))
                Files.createDirectory(pluginsPath);

            final Path modulesPath = Paths.get("modules/");
            if (!Files.exists(modulesPath))
                Files.createDirectory(modulesPath);

            final Path downloadsPath = Paths.get("downloads/");
            if (!Files.exists(downloadsPath))
                Files.createDirectory(downloadsPath);

            final Path staticPath = Paths.get("static/");
            if (!Files.exists(staticPath))
                Files.createDirectory(staticPath);

            final Path serverPath = Paths.get("servers/");
            if (!Files.exists(serverPath))
                Files.createDirectory(serverPath);

        } catch (Exception ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Configurations and folders loaded!");
    }

    private void registerListeners() {
        CloudAPI.getInstance().getPacketRegistry()
                .registerListener(new PacketPlayOutConnectionSuccessListener())
                .registerListener(new PacketPlayOutUpdateResourceListener());
    }

    private void connectToMaster() {
        final SlaveConfiguration.NetworkConfig networkSettings = this.configuration.getNetworkSettings();
        new Thread(new ClientNetworkImpl(networkSettings.getHostName(), networkSettings.getPort(), this.object),
                "network_" + this.object.getName().replace("-", "_") + "_thread").start();
    }

    private void runResourceUpdater() {
        int usedMemory = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L);

        final Resource resource = new Resource(this.object.getName(), this.system.getSystemLoadAverage(),
                usedMemory, this.configuration.getMaxRamSizeToUse());
        CloudAPI.getInstance().getResourceRegistry().registerResource(resource);

        final Timer timer = new Timer("resource-updater-thread");
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                final Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection("CLOUD");

                CloudAPI.getInstance().getResourceRegistry().getResource(resource.getName())
                        .setResource(system.getSystemLoadAverage(),
                                (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L),
                                configuration.getMaxRamSizeToUse());

                if (connection == null || connection.getChannel().isRemoved())
                    return;

                connection.getChannel().writeAndFlush(new PacketPlayInResourceInfo(
                        CloudAPI.getInstance().getResourceRegistry().getResource(resource.getName())));
            }

        }, 5000, 100);
    }
}