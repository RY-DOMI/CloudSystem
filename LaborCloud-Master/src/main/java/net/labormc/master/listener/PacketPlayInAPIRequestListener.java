package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.api.APIRequestTypes;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayInAPIRequest;
import net.labormc.cloudapi.network.protocol.packets.api.PacketPlayOutAPIResponse;
import net.labormc.cloudapi.server.entities.Tablist;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.master.Master;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInAPIRequestListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInAPIRequest packet, ChannelHandlerContext ctx) {
        final UUID uniqueId = packet.getUniqueId();
        final APIRequestTypes type = packet.getType();
        final String[] args = packet.getArgs();

        switch (type) {
            case WHITELIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    Master.getInstance().getConfiguration().getServerSettings().getWhitelist()));
            case TABLIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, Tablist.class,
                    new Tablist(Master.getInstance().getConfiguration().getServerSettings().getHeader(),
                            Master.getInstance().getConfiguration().getServerSettings().getFooter())));
            case CLOUDPLAYER -> {
                String command = args[0].replaceAll("\\[", "").replaceAll("]", "");
                String arg = args[1].replaceAll("\\[", "").replaceAll("]", "");
                ;
                CloudPlayer cloudPlayer = switch (command.toUpperCase()) {
                    case "NAME" -> CloudAPI.getInstance().getCloudPlayerRegistry().getCloudPlayer(arg);
                    case "UUID" -> CloudAPI.getInstance().getCloudPlayerRegistry().getCloudPlayer(UUID.fromString(arg));
                    default -> null;
                };
                ctx.writeAndFlush(new PacketPlayOutAPIResponse(packet.getUniqueId(), CloudPlayer.class,
                        cloudPlayer));
            }
            case PLAYERCOUNT -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, Integer.class,
                    CloudAPI.getInstance().getCloudPlayerRegistry().getAll().size()));
            case MAXPLAYERS -> {
                final Template proxyTemplate = CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy");
                AtomicInteger maxPlayers = new AtomicInteger(0);
                CloudAPI.getInstance().getServerRegistry().getAll(proxyTemplate).forEach(cloudServer ->
                        maxPlayers.addAndGet(proxyTemplate.getMaxPlayers()));
                ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, Integer.class,
                        maxPlayers.get()));
            }
            case MOTD_LIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    Master.getInstance().getConfiguration().getServerSettings().getMotdList()));
            case SIGNLAYOUT_LIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    Master.getInstance().getConfiguration().getServerSettings().getSignLayoutList()));
            case SIGNLOCATION_LIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    Master.getInstance().getConfiguration().getServerSettings().getSignLocationList()));
            case PLAYERINFO_LIST -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    Master.getInstance().getConfiguration().getServerSettings().getPlayerInfoList()));
            case MAINTENANCE -> {
                if (args.length == 1) {
                    switch (args[0].toUpperCase()) {
                        case "ENABLE" -> {
                            if (Master.getInstance().getConfiguration().getServerSettings().isMaintenance())
                                break;
                            Master.getInstance().getConfiguration().getServerSettings().setMaintenance(true);
                            Master.getInstance().updateConfig();
                            Master.getInstance().updateMaintenance(true);
                        }
                        case "DISABLE" -> {
                            if (!(Master.getInstance().getConfiguration().getServerSettings().isMaintenance()))
                                break;
                            Master.getInstance().getConfiguration().getServerSettings().setMaintenance(false);
                            Master.getInstance().updateConfig();
                            Master.getInstance().updateMaintenance(false);
                        }
                        case "CURRENTSTATE" -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, Boolean.class,
                                Master.getInstance().getConfiguration().getServerSettings().isMaintenance()));
                    }
                    break;
                }
                if (args.length == 2) {
                    final String templateName = args[1];
                    Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(templateName);

                    if (template == null)
                        break;

                    switch (args[0].toUpperCase()) {
                        case "ENABLE" -> {
                            if (template.isMaintenance())
                                break;
                            template.setMaintenance(true);
                            Master.getInstance().setMaintenance(template, true);
                        }
                        case "DISABLE" -> {
                            if (!template.isMaintenance())
                                break;
                            template.setMaintenance(false);
                            Master.getInstance().setMaintenance(template, false);
                        }
                        case "CURRENTSTATE" -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, Boolean.class,
                                template.isMaintenance()));
                    }
                }
            }
            case MAINTENANCE_KICK_MESSAGE -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, String.class,
                    Master.getInstance().getConfiguration().getServerSettings().getMaintenanceKickMessage()));
            case SERVER_INFO_BY_TEMPLATE -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                    CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate(args[0]))));
            case SERVER_INFO -> {
                if (args[0].equalsIgnoreCase("update")) {
                    System.out.println(args[1]);
                    final MinecraftServer server = CloudAPI.getInstance().getGson().fromJson(args[1], MinecraftServer.class);

                    CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                            .setOnlineCount(server.getOnlineCount());
                    CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                            .setExtra(server.getExtra());
                    if (server.getGame() != null) {
                        CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                                .setGame(server.getGame());
                    }
                } else if (args[0].equalsIgnoreCase("get")) {
                    final String name = args[1];

                    final MinecraftServer server = CloudAPI.getInstance().getServerRegistry().getServer(name, MinecraftServer.class);
                    if (server != null) {
                        ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, String.class,
                                server));
                    }
                }
            }
            case SERVER_INFO_BY_GAME -> ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, MinecraftServer.class,
                    CloudAPI.getInstance().getServerRegistry().getAll().stream()
                            .filter(server -> {
                                if (server instanceof final MinecraftServer minecraftServer)
                                    return minecraftServer.getGame() != null && minecraftServer.getGame().getName().equalsIgnoreCase(args[0]);

                                return false;
                            }).collect(Collectors.toList())));

            case GAME_SERVER_INFO ->
                ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, MinecraftServer.class,
                        CloudAPI.getInstance().getServerRegistry().getAll().stream()
                                .filter(server -> {
                                    if (server instanceof final MinecraftServer minecraftServer)
                                        return minecraftServer.getGame() != null;

                                    return false;
                                }).collect(Collectors.toList())));

            case TEMPLATE_LIST ->
                ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId,  List.class,
                        CloudAPI.getInstance().getTemplateRegistry().getAll()));

            case MESSAGE_LIST -> {
                if (args[0].equalsIgnoreCase("add")) {
                    Master.getInstance().addMessage(CloudAPI.getInstance().getGson().fromJson(args[1], Message.class));
                    return;
                }

                ctx.writeAndFlush(new PacketPlayOutAPIResponse(uniqueId, List.class,
                        CloudAPI.getInstance().getMessageRegistry().getAll()));
            }

        }
    }
}
