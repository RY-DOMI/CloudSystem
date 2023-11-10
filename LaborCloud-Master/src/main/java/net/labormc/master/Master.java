package net.labormc.master;

import lombok.Getter;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.network.protocol.packets.maintenance.PacketPlayOutUpdateMaintenance;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutReloadProxy;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.service.IService;
import net.labormc.cloudapi.sign.SignLayout;
import net.labormc.cloudapi.sign.SignLocation;
import net.labormc.cloudapi.sign.enums.SignLayoutStates;
import net.labormc.master.cloudflare.CloudFlareManager;
import net.labormc.master.command.ReloadCommand;
import net.labormc.master.command.ServerCommand;
import net.labormc.master.command.TemplateCommand;
import net.labormc.master.database.DatabaseManager;
import net.labormc.master.database.group.GroupLoader;
import net.labormc.master.database.user.UserLoader;
import net.labormc.master.database.user.web.WebConfiguration;
import net.labormc.master.database.user.web.WebServer;
import net.labormc.master.listener.*;
import net.labormc.master.network.ServerNetwork;
import net.labormc.master.server.QueueServer;
import net.labormc.master.server.ServerQueue;
import net.labormc.master.server.sign.SignManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public class Master implements IService {

    @Getter
    private static Master instance;

    private final Map<SignLayoutStates, SignLayout> signLayoutMap = new LinkedHashMap<>();
    private final Map<String, List<SignLocation>> signLocationMap = new LinkedHashMap<>();
    private final List<SignLocation> signLocationList = new LinkedList<>();

    private final List<QueueServer> queuedServerList = new LinkedList<>();

    private MasterConfiguration configuration;
    private CloudFlareManager cloudFlareManager;
    private SignManager signManager;
    private ServerQueue serverQueue;

    private DatabaseManager databaseManager;
    private UserLoader userLoader;
    private GroupLoader groupLoader;

    @Override
    public void onInit() {
        instance = this;
        this.loadConfiguration();

        this.databaseManager = new DatabaseManager(this.configuration.getDatabaseSettings());
        this.userLoader = new UserLoader(this.databaseManager);
        this.groupLoader = new GroupLoader(this.databaseManager);

        this.cloudFlareManager = new CloudFlareManager();
        this.cloudFlareManager.init();

        this.serverQueue = new ServerQueue();
        this.serverQueue.start();
        this.signManager = new SignManager();
    }

    @Override
    public void onEnable() {
        this.registerListeners();
        this.registerCommands();
        this.connectToDatabase();
        this.runNetwork();
        this.runWebServer();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onReload() {
        this.signLayoutMap.clear();
        this.signLocationMap.clear();
        this.signLocationList.clear();

        this.loadConfiguration();

        CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy"))
                .forEach(proxy -> CloudAPI.getInstance().getConnectionRegistry().getConnection(proxy.getName())
                        .getChannel().writeAndFlush(new PacketPlayOutReloadProxy()));
        return true;
    }

    private void loadConfiguration() {
        try {
            final File configFile = new File("config.json");
            if (!configFile.exists())
                CloudAPI.getInstance().document().append("config", new MasterConfiguration(
                        new MasterConfiguration.NetworkSettings(
                                "127.0.0.1",
                                6734,
                                new LinkedList<>()),
                        new MasterConfiguration.ServerSettings(
                                false,
                                "&b&lLaborMC.net\n&c&lMainenance",
                                "Header", "Footer",
                                new LinkedList<>(),
                                new LinkedList<>(),
                                new LinkedList<>(),
                                new LinkedList<>(),
                                new LinkedList<>(),
                                new LinkedList<>()),
                        new MasterConfiguration.CloudSettings(
                                new LinkedList<>(),
                                new LinkedList<>(),
                                new LinkedList<>()
                        ),
                        new WebConfiguration(
                                4999,
                                "secret_token",
                                "cloud/v1"
                        ),
                        new MasterConfiguration.DatabaseSettings(
                                "127.0.0.1",
                                27017,
                                "admin",
                                "pwd",
                                "labormc_network"),
                        new LinkedList<>())).save(configFile);
            this.configuration = CloudAPI.getInstance().document().load(configFile).get("config", MasterConfiguration.class);
            this.configuration.getServerSettings().getSignLayoutList().forEach((signLayout ->
                    this.signLayoutMap.put(signLayout.getState(), signLayout)));
            System.out.println(MessageFormat.format(" - ({0}) SignLayouts loaded!", signLayoutMap.size()));

            this.configuration.getServerSettings().getSignLocationList().forEach((signLocation -> {
                this.signLocationMap.put(signLocation.getGameName(), new LinkedList<>());

                this.signLocationMap.get(signLocation.getGameName()).add(signLocation);
                this.signLocationList.add(signLocation);
            }));
            System.out.println(MessageFormat.format(" - ({0}) SignLocations loaded!", signLocationList.size()));

            this.configuration.getServerSettings().getTemplateList().forEach(template -> {
                CloudAPI.getInstance().getTemplateRegistry().registerTemplate(template);
            });
            System.out.println(MessageFormat.format(" - ({0}) Templates loaded!",
                    CloudAPI.getInstance().getTemplateRegistry().getAll().size()));

            this.configuration.getMessageList().forEach(message -> {
                CloudAPI.getInstance().getMessageRegistry().registerMessage(message);
            });
            System.out.println(MessageFormat.format(" - ({0}) Messages loaded!",
                    CloudAPI.getInstance().getMessageRegistry().getAll().size()));

            final Path templatesPath = Paths.get("templates/");
            if (!Files.exists(templatesPath))
                Files.createDirectory(templatesPath);

            final Path pluginsPath = Paths.get("plugins/");
            if (!Files.exists(pluginsPath))
                Files.createDirectory(pluginsPath);

            final Path modulesPath = Paths.get("modules/");
            if (!Files.exists(modulesPath))
                Files.createDirectory(modulesPath);

            final Path deployPath = Paths.get("deploy/");
            if (!Files.exists(deployPath))
                Files.createDirectory(deployPath);

        } catch (Exception ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Configurations and folders loaded!");
    }

    private void registerListeners() {
        CloudAPI.getInstance().getPacketRegistry()
                .registerListener(new PacketPlayInClientConnectListener())
                .registerListener(new PacketPlayInAPIRequestListener())
                .registerListener(new PacketPlayInResourceInfoListener())
                .registerListener(new PacketPlayInCloudPlayerDisconnectedListener())
                .registerListener(new PacketPlayInCloudPlayerUpdateListener())
                .registerListener(new PacketPlayInCloudPlayerConnectedListener())
                .registerListener(new PacketPlayInDeleteServerListener())
                .registerListener(new PacketPlayInRequestServerListener())
                .registerListener(new PacketPlayInStartServerListener())
                .registerListener(new PacketPlayInUpdateServerInfoListener());
    }

    private void registerCommands() {
        CloudAPI.getInstance().getCloudCommandRegistry()
                .registerCommand(new ReloadCommand())
                .registerCommand(new ServerCommand())
                .registerCommand(new TemplateCommand());
    }

    private void connectToDatabase() {
        this.databaseManager.connect();
        System.out.println(" - Successfully connected to database!");
    }

    private void runNetwork() {
        final MasterConfiguration.NetworkSettings networkSettings = this.configuration.getNetworkSettings();
        new Thread(new ServerNetwork(networkSettings.getHostName(), networkSettings.getPort()),
                "network_server_thread").start();
    }

    private void runWebServer() {
        new Thread(new WebServer(this.configuration.getWebSettings()),
                "web_server_thread").start();
    }

    public void updateConfig() {
        final File configFile = new File("config.json");
        final AbstractDocument document = CloudAPI.getInstance().document().load(configFile);

        document.append("config", this.configuration);
        document.save(configFile);
    }

    public void updateMaintenance(boolean enabled) {
        CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy"))
                .forEach(proxy -> CloudAPI.getInstance().getConnectionRegistry().getConnection(proxy.getName()).getChannel()
                        .writeAndFlush(new PacketPlayOutUpdateMaintenance(enabled)));
    }

    public void setMaintenance(Template template, boolean state) {
        CloudAPI.getInstance().getServerRegistry().getAll(template).forEach((server) -> {
            CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName()).setMaintenance(state);
            this.configuration.getServerSettings().getTemplateList().set(this.configuration.getServerSettings().getTemplateList().indexOf(template),
                    CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName()));
            this.updateConfig();
        });
    }

    public void addMessage(Message message) {
        if (!this.configuration.getMessageList().contains(message)) {
            this.configuration.getMessageList().add(message);
            this.configuration.getMessageList().forEach(msg -> CloudAPI.getInstance().getMessageRegistry().registerMessage(msg));
        }

        this.configuration.getMessageList().set(this.configuration.getMessageList().indexOf(this.configuration.getMessageList().stream()
                .filter(msg -> Objects.equals(msg.getName(), message.getName()))
                .findFirst()
                .orElse(null)), message);

        this.updateConfig();
    }
}
