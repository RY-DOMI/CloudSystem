package net.labormc.master.listener;

import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.cloudapi.network.connection.ConnectionTypes;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayInClientConnect;
import net.labormc.cloudapi.network.protocol.packets.channel.PacketPlayOutConnectionSuccess;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutRegisterServer;
import net.labormc.cloudapi.server.template.enums.TemplateTypes;
import net.labormc.master.Master;

import java.text.MessageFormat;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketPlayInClientConnectListener implements IPacketListener {

    @IPacketHandler
    public void on(PacketPlayInClientConnect packet, ChannelHandlerContext ctx) {
        Object object = packet.getObject();

        if (object instanceof final Slave slave) {
            if (CloudAPI.getInstance().getClientRegistry().getClient(slave.getUniqueId()) != null) {
                ctx.channel().disconnect();
                return;
            }

            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setUniqueId(slave.getUniqueId());
            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setName(slave.getName());
            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setType(ConnectionTypes.SLAVE);

            CloudAPI.getInstance().getClientRegistry().registerClient(slave);

            final Slave s = Master.getInstance().getConfiguration().getNetworkSettings().getSlaveList().stream()
                    .filter(slave1 -> slave1.getName().equalsIgnoreCase(slave.getName()))
                    .findFirst()
                    .orElse(null);

            if (s == null) {
                Master.getInstance().getConfiguration().getNetworkSettings().getSlaveList().add(slave);
                Master.getInstance().updateConfig();
            }
            ctx.writeAndFlush(new PacketPlayOutConnectionSuccess());

            System.out.println(MessageFormat.format("Slave [connectionId={0}#{1}/address={2}] logged in.",
                    slave.getName(), slave.getUniqueId(), slave.getAddress().toString().replaceFirst("/",
                            "")));
        } else if (object instanceof final ProxyServer proxy) {

            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setName(proxy.getName());
            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setType(ConnectionTypes.PROXY_SERVER);

            CloudAPI.getInstance().getServerRegistry().getServer(proxy.getName()).setOnlineCount(proxy.getOnlineCount());
            CloudAPI.getInstance().getServerRegistry().getServer(proxy.getName()).setHostName(proxy.getHostName());
            CloudAPI.getInstance().getServerRegistry().getServer(proxy.getName()).setPort(proxy.getPort());

            if (!proxy.getName().equalsIgnoreCase("Proxy-1"))
                Master.getInstance().getCloudFlareManager().registerProxy(proxy);

            CloudAPI.getInstance().getServerRegistry().getAll().forEach(server -> {
                if (CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName()).getType() == TemplateTypes.PROXY ||
                        server.getHostName() == null)
                    return;

                CloudAPI.getInstance().getConnectionRegistry().getConnection(proxy.getName()).getChannel()
                        .writeAndFlush(new PacketPlayOutRegisterServer(MinecraftServer.class, server));
            });

            ctx.writeAndFlush(new PacketPlayOutConnectionSuccess());
            System.out.println(MessageFormat.format("Proxy [serviceId={0}#{1}/address={2}] logged in.",
                    proxy.getName(), proxy.getUniqueId().toString(), proxy.getAddress().toString().replaceFirst("/",
                            "")));
        } else if (object instanceof final MinecraftServer server) {
            if (CloudAPI.getInstance().getServerRegistry().getServer(server.getName()) == null) {
                ctx.channel().close();
                return;
            }

            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setName(server.getName());
            CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx).setType(ConnectionTypes.MINECRAFT_SERVER);

            CloudAPI.getInstance().getServerRegistry().getServer(server.getName()).setHostName(server.getHostName());
            CloudAPI.getInstance().getServerRegistry().getServer(server.getName()).setPort(server.getPort());

            CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                    .setOnlineCount(server.getOnlineCount());
            CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                    .setExtra(server.getExtra());
            CloudAPI.getInstance().getServerRegistry().getServer(server.getName(), MinecraftServer.class)
                    .setGame(server.getGame());

            if (!CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy"))
                    .isEmpty())
                CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy"))
                        .forEach(proxy -> {
                            if (CloudAPI.getInstance().getConnectionRegistry().getConnection(proxy.getName()) == null)
                                return;
                            CloudAPI.getInstance().getConnectionRegistry().getConnection(proxy.getName()).getChannel()
                                    .writeAndFlush(new PacketPlayOutRegisterServer(MinecraftServer.class, server));
                        });

            ctx.writeAndFlush(new PacketPlayOutConnectionSuccess());
            System.out.println(MessageFormat.format("Server [serviceId={0}#{1}/address={2}] logged in.",
                    server.getName(), server.getUniqueId().toString(), server.getAddress().toString().replaceFirst("/",
                            "")));
        }
    }
}
