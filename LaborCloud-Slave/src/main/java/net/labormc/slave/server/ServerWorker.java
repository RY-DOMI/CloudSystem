package net.labormc.slave.server;

import lombok.Getter;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.server.ServerConfig;
import net.labormc.cloudapi.server.game.Game;
import net.labormc.cloudapi.server.game.GameStates;
import net.labormc.cloudapi.server.game.map.ServerMap;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.server.template.enums.TemplateStates;
import net.labormc.cloudapi.server.template.enums.TemplateTypes;
import net.labormc.slave.Slave;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerWorker {

    @Getter
    private final Map<String, Process> processMap = new LinkedHashMap<>();
    private final Map<String, UUID> uniqueMap = new LinkedHashMap<>();
    private final Map<String, Integer> portMap = new LinkedHashMap<>();

    public void startServer(String name, UUID uniqueId, Template template) {
        try {
            this.copyFiles(name, uniqueId, template);
            final ProcessBuilder builder = this.buildProcess(template);

            final TemplateStates state = template.getState();

            File file = null;
            if (state == TemplateStates.LOBBY || state == TemplateStates.DYNAMIC)
                file = new File("servers/" + template.getName()
                        + "/" + name + "#"
                        + uniqueId.toString());
            else
                file = new File("static/" + template.getName()
                        + "/" + name);
            builder.directory(file);

            final String address = this.prepareProperties(name, uniqueId, template, 0);

            final Process process = builder.start();
            this.processMap.put(name, process);
            this.uniqueMap.put(name, uniqueId);

            System.out.println(MessageFormat.format("Server [serviceId={0}#{1}/template={2}/address={3}] started!", name,
                    uniqueId.toString(), template.getName(), address));
        } catch (Exception ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startProxy(String name, UUID uniqueId, Template template, int port) {
        try {
            this.copyFiles(name, uniqueId, template);
            final ProcessBuilder builder = this.buildProcess(template);

            final TemplateStates state = template.getState();

            File file = null;
            if (state == TemplateStates.DYNAMIC)
                file = new File("servers/" + template.getName()
                        + "/" + name + "#"
                        + uniqueId.toString());
            else if (state == TemplateStates.STATIC)
                file = new File("static/" + template.getName()
                        + "/" + name);
            builder.directory(file);

            final String address = this.prepareProperties(name, uniqueId, template, port);

            final Process process = builder.start();
            this.processMap.put(name, process);
            this.uniqueMap.put(name, uniqueId);

            System.out.println(MessageFormat.format("Proxy [serviceId={0}#{1}/template={2}/address={3}] started!", name,
                    uniqueId.toString(), template.getName(), address));
        } catch (Exception ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopServer(String name, Template template) {
        final TemplateStates state = template.getState();
        final UUID uniqueId = this.uniqueMap.remove(name);

        final Process process = this.processMap.remove(name);
        process.destroy();

        if (state == TemplateStates.LOBBY || state == TemplateStates.DYNAMIC) {
            if (template.isBlockPort()) {
                final int port = this.portMap.remove(name);
                Slave.getInstance().getPortBlocker().unblockPort(port);
            }
            final File file = new File("servers/"
                    + template.getName() + "/" + name + "#" + uniqueId.toString());
            this.deleteDirectory(file);
        } else
        if (template.isBlockPort()) {
            final int port = this.portMap.remove(name);
            Slave.getInstance().getPortBlocker().unblockPort(port);
        }

        System.out.println(MessageFormat.format("Server [serviceId={0}#{1}/template={2}] stopped!", name,
                uniqueId.toString(), template.getName()));
    }

    private void copyFiles(String name, UUID uniqueId, Template template) {
        final TemplateStates state = template.getState();
        final File templateFolder = new File("templates/" + template.getName());
        final File pluginsFolder = new File("plugins/");
        final File modulesFolder = new File("modules/");

        if (state == TemplateStates.LOBBY || state == TemplateStates.DYNAMIC)
            try {
                final File file = new File("servers/"
                        + template.getName() + "/" + name + "#"
                        + uniqueId.toString());
                if (!file.exists())
                    file.mkdirs();
                else {
                    FileUtils.deleteQuietly(file);
                    file.mkdirs();
                }
                FileUtils.copyDirectory(templateFolder, file);
                loadAssets(template, pluginsFolder, modulesFolder, file);
            } catch (IOException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        else
            try {
                final File file = new File("static/" + template.getName() + "/" + name);
                if (!file.exists())
                    file.mkdirs();
                FileUtils.copyDirectory(templateFolder, new File("static/" + template.getName() + "/" + name));

                loadAssets(template, pluginsFolder, modulesFolder, file);
            } catch (IOException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    private void loadAssets(Template template, File pluginsFolder, File modulesFolder, File file) {
        template.getPluginList().forEach(plugin -> {
            try {
                FileUtils.copyFileToDirectory(new File(pluginsFolder, plugin + ".jar"),
                        new File(file, "plugins"));
            } catch (IOException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        template.getModuleList().forEach(module -> {
            try {
                FileUtils.copyFileToDirectory(new File(modulesFolder, module + ".jar"),
                        new File(file, "modules"));
            } catch (IOException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private ProcessBuilder buildProcess(Template template) {
        final ProcessBuilder builder = new ProcessBuilder();
        final StringBuilder command = new StringBuilder(MessageFormat.format("java -XX:+UseG1GC -XX:MaxGCPauseMillis=50"
                        + " -XX:MaxPermSize=256M -XX:-UseAdaptiveSizePolicy -XX:CompileThreshold=100"
                        + " -Dio.netty.leakDetectionLevel=DISABLED -Dfile.encoding=UTF-8 -Dio.netty.maxDirectMemory=0"
                        + " -Dio.netty.recycler.maxCapacity=0 -Dio.netty.recycler.maxCapacity.default=0"
                        + " -Djline.terminal=jline.UnsupportedTerminal -Xmx" + template.getMaxMemory() + "M -Xms"
                        + template.getMemory() + "M -jar ", template.getMaxMemory(),
                template.getMemory()));

        switch (template.getType()) {

            case PROXY:
                command.append("BungeeCord.jar -o true -p");
                break;

            case SERVER:
                command.append("spigot.jar nogui");
                break;

        }

        return builder.command(command.toString().split(" "));
    }

    private String prepareProperties(String name, UUID uniqueId, Template template, int port) throws Exception {
        final File file = new File((template.getState() == TemplateStates.STATIC ? "static/" : "servers/")
                + template.getName() + "/" + name + "#" + uniqueId.toString());
        String address = InetAddress.getLocalHost().getHostAddress() + ":";

        final String[] split = address.split(":");
        if (template.getType() == TemplateTypes.SERVER)
            port = Slave.getInstance().getPortBlocker().generateRandomPort();
        address += port;

        File configFile;

        switch (template.getType()) {

            case PROXY:
                this.rewriteFile(new File(file, "config.yml"), address);

                configFile = new File(file, "configs/config.json");
                CloudAPI.getInstance().document().append("cloud", new ServerConfig(Slave.getInstance().getConfiguration()
                        .getNetworkSettings().getHostName(), Slave.getInstance().getConfiguration().getNetworkSettings().getPort()))
                        .append("server", new ProxyServer(uniqueId, name, split[0], port,
                        0, template.getName(),
                                Slave.getInstance().getObject().getUniqueId())).save(configFile);

                break;

            case SERVER:
                final File configs = new File(file, "configs");
                configs.mkdirs();

                final Properties properties = new Properties();
                final File serverProperties = new File(file, "configs/server.properties");

                if (serverProperties.exists())
                    properties.load(new FileReader(serverProperties));
                else
                    serverProperties.createNewFile();

                properties.setProperty("server-name", name);
                properties.setProperty("server-id", uniqueId.toString());
                properties.setProperty("server-ip", split[0]);
                properties.setProperty("server-port", String.valueOf(port));
                properties.setProperty("online-mode", "false");
                properties.setProperty("spawn-protection", "0");
                properties.setProperty("motd", "");
                properties.setProperty("level-type", "FLAT");
                properties.setProperty("allow-nether", "false");
                properties.setProperty("announce-player-achievements", "false");
                properties.setProperty("difficulty", "0");
                properties.setProperty("spawn-animals", "false");
                properties.setProperty("spawn-monsters", "false");
                properties.setProperty("max-players", String.valueOf(template.getMaxPlayers() + template.getSpectatingPlayers()));

                properties.store(new FileWriter(serverProperties), "Cloud-Server Properties");

                this.portMap.put(name, port);
                Slave.getInstance().getPortBlocker().blockPort(port);
                configFile = new File(file, "configs/config.json");

                CloudAPI.getInstance().document().append("cloud", new ServerConfig(Slave.getInstance().getConfiguration()
                        .getNetworkSettings().getHostName(), Slave.getInstance().getConfiguration().getNetworkSettings().getPort()))
                        .append("server", new MinecraftServer(uniqueId, name, split[0], port, 0,
                        template.getName(), Slave.getInstance().getObject().getUniqueId(),
                        "",
                        new Game(UUID.randomUUID().toString().split("-")[0],
                                template.getName().split(" ")[0], GameStates.STARTING,
                                new ServerMap(template.getName(), "default",
                                        Collections.singletonList("LaborMC.net Buildteam"))))).save(configFile);
                break;

        }

        return address;
    }

    private void rewriteFile(File file, String host) throws Exception {
        file.setReadable(true);
        final FileInputStream in = new FileInputStream(file);
        final List<String> list = new LinkedList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String input;
        boolean value = false;
        while ((input = reader.readLine()) != null)
            if (value) {
                list.add("  host: " + host + "\n");
                value = false;
            } else
            if (input.startsWith("  query_enabled")) {
                list.add(input + "\n");
                value = true;
            } else
                list.add(input + "\n");
        file.delete();
        file.createNewFile();
        file.setReadable(true);

        final FileOutputStream out = new FileOutputStream(file);
        final PrintWriter w = new PrintWriter(out);

        list.forEach(s -> {
            w.write(s);
            w.flush();
        });
        reader.close();
        w.close();
    }

    private void deleteDirectory(File file) {
        try {
            final Process exec = Runtime.getRuntime().exec(new String[]{"bash", "-c", "rm -r " + file.getPath()});
            exec.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
