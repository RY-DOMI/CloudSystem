package net.labormc.master.server.sign;

import lombok.RequiredArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutUpdateSign;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.game.GameStates;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.sign.CloudSign;
import net.labormc.cloudapi.sign.GameSign;
import net.labormc.cloudapi.sign.SignLayout;
import net.labormc.cloudapi.sign.SignLocation;
import net.labormc.cloudapi.sign.enums.SignLayoutStates;
import net.labormc.master.Master;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class SignManager {

    private final List<GameSign> signs = new CopyOnWriteArrayList<>();

    private final Map<Template, List<GameSign>> templateSigns = new ConcurrentHashMap<>();

    public SignManager() {
        this.templateSigns.clear();

        Master.getInstance().getSignLocationList().forEach((location) -> {
            final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(location.getGameName());
            final GameSign gameSign = new GameSign(UUID.randomUUID(),
                    new CloudSign(SignLayoutStates.SEARCHING, null), location.getGameName(), location);

            if (!templateSigns.containsKey(template))
                this.templateSigns.put(template, new ArrayList<>());

            this.templateSigns.get(template).add(gameSign);
            this.signs.add(gameSign);
        });

        final SignWorker signWorker = new SignWorker();
        signWorker.start();

        new Thread(() -> {
            for (GameSign sign : signs) {
                if (sign.getCloudSign() == null)
                    return;

                if (sign.getCloudSign().getServer() != null)
                    sign.getCloudSign().setServer(null);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SignManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, "CloudSign-Timer-Thread").start();
    }

    @RequiredArgsConstructor
    private class SignWorker extends Thread {

        private final Map<GameSign, Integer> currentCountMap = new HashMap<>();

        @Override
        public void run() {
            while (true) {
                for (CloudServer s : CloudAPI.getInstance().getServerRegistry().getAll()) {
                    if (!(s instanceof MinecraftServer))
                        continue;
                    MinecraftServer server = (MinecraftServer) s;
                    final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName());

                    if (!(templateSigns.containsKey(template)))
                        continue;
                    GameSign freeSign = isServerOnSign(server) ? getSign(server) : getFreeSign(template.getName());

                    if (freeSign == null)
                        continue;
                    int indexTemplate = templateSigns.get(template).indexOf(freeSign);
                    int indexSigns = signs.indexOf(freeSign);

                    freeSign.getCloudSign().setServer(server);

                    SignLayoutStates state = SignLayoutStates.SEARCHING;

                    if (template.isMaintenance())
                        state = SignLayoutStates.MAINTENANCE;
                    else if (server.getGame().getState() == GameStates.LOBBY)
                        state = (server.getOnlineCount() >= template.getMaxPlayers()) ? SignLayoutStates.FULL : SignLayoutStates.LOBBY;

                    freeSign.getCloudSign().setState(state);

                    templateSigns.get(template).get(indexTemplate).setCloudSign(freeSign.getCloudSign());
                    signs.get(indexSigns).setCloudSign(freeSign.getCloudSign());
                }

                for (GameSign sign : signs) {
                    if (!currentCountMap.containsKey(sign))
                        currentCountMap.put(sign, 0);
                    final SignLayout signLayout = Master.getInstance().getSignLayoutMap().get(sign.getCloudSign().getState());
                    final SignLayout.Layout layout = getNext(sign, signLayout);

                    if (sign.getCloudSign().getServer() == null || sign.getCloudSign().getState() == SignLayoutStates.SEARCHING)
                        updateSign(sign, signLayout.getBlockColor(), getLines(layout));
                    else
                        updateSign(sign, signLayout.getBlockColor(), getLines(Master.getInstance().getSignLayoutMap().get(sign.getCloudSign().getState()).getLayouts().get(0)));

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SignManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private SignLayout.Layout getNext(GameSign sign, SignLayout signLayout) {
            int currentCount = currentCountMap.get(sign);

            currentCount++;
            if (currentCount >= signLayout.getLayouts().size())
                currentCount = 0;

            currentCountMap.put(sign, currentCount);
            return signLayout.getLayouts().get(currentCount);
        }

        private String[] getLines(SignLayout.Layout layout) {
            return new String[]{layout.getLine1(), layout.getLine2(), layout.getLine3(), layout.getLine4()};
        }

    }

    public void clearSign(CloudServer server) {
        for (GameSign sign : signs)
            if (sign.getCloudSign().getServer() != null && sign.getCloudSign().getServer().getName().equalsIgnoreCase(server.getName())) {
                sign.getCloudSign().setServer(null);
                sign.getCloudSign().setState(SignLayoutStates.SEARCHING);
            }
    }

    private GameSign getFreeSign(String gameName) {
        for (GameSign sign : signs)
            if (sign.getCloudSign().getServer() == null && sign.getGameName().equalsIgnoreCase(gameName))
                return sign;
        return null;
    }

    private GameSign getSign(MinecraftServer server) {
        GameSign gameSign = null;
        final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName());

        for (GameSign sign : templateSigns.get(template))
            if (sign.getCloudSign().getServer() != null && sign.getCloudSign().getServer().getName().equalsIgnoreCase(server.getName())) {
                gameSign = sign;
                break;
            }
        return gameSign;
    }

    private boolean isServerOnSign(MinecraftServer server) {
        return signs.stream().anyMatch((sign) -> {
            return sign.getCloudSign().getServer() != null
                    && sign.getCloudSign().getServer().getName().equals(server.getName());
        });
    }

    private void updateSign(GameSign gameSign, byte color, String[] layout) {
        final String[] layoutLines = layout.clone();
        Template template = null;

        for (int i = 0; i < layout.length; i++) {
            if (gameSign.getCloudSign() == null)
                break;
            final CloudSign cloudSign = gameSign.getCloudSign();
            layoutLines[i] = (layoutLines[i]
                    .replace("%gameName%", gameSign.getGameName())).replaceAll("&", "§");

            if (cloudSign.getServer() != null) {
                template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(cloudSign.getServer().getTemplateName());
                layoutLines[i] = (layoutLines[i]
                        .replace("%serverName%", cloudSign.getServer().getName())
                        .replace("%online%", Integer.toString(cloudSign.getServer().getOnlineCount()))
                        .replace("%max%", Integer.toString(template.getMaxPlayers()))
                        //.replace("%map%", ((cloudSign.getServer().getGame() != null) ? cloudSign.getServer().getGame().getMap().getName() : ""))
                        .replace("%extra%", cloudSign.getServer().getExtra()) /*.replace("%currentState%", cloudSign.getServer().getGameState().name())*/).replaceAll("&", "§");
            }
        }
        final SignLocation location = gameSign.getLocation();

        String serverName = gameSign.getCloudSign().getServer() == null ? "" : gameSign.getCloudSign().getServer().getName();

        GameStates state = gameSign.getCloudSign().getServer() == null
                ? GameStates.STARTING : ((gameSign.getCloudSign().getServer().getGame().getState()) == null)
                ? GameStates.NONE : gameSign.getCloudSign().getServer().getGame().getState();

        int onlinePlayers = gameSign.getCloudSign().getServer() == null ? 0 : gameSign.getCloudSign().getServer().getOnlineCount();
        int maxPlayers = gameSign.getCloudSign().getServer() == null ? 0 : Objects.requireNonNull(template).getMaxPlayers();

        PacketPlayOutUpdateSign packet = new PacketPlayOutUpdateSign(serverName, state, color, onlinePlayers, maxPlayers, location.getX(), location.getY(), location.getZ(), layoutLines);

        this.getLobbies().forEach(lobby -> {
            CloudAPI.getInstance().getConnectionRegistry().getConnection(lobby.getName())
                    .getChannel().writeAndFlush(packet);
        });
    }

    public List<CloudServer> getLobbies() {
        final List<CloudServer> list = new LinkedList<>();
        CloudAPI.getInstance().getTemplateRegistry().getAll().forEach(template -> {
            if (template.getName().toLowerCase().contains("lobby"))
                for (CloudServer server : CloudAPI.getInstance().getServerRegistry().getAll(template))
                    if (CloudAPI.getInstance().getConnectionRegistry().getConnection(server.getName()) != null)
                        list.add(server);
        });

        return list;
    }
}
