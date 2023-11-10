package net.labormc.master.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutUnregisterServer;
import net.labormc.master.Master;

import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerNetworkHandler extends SimpleChannelInboundHandler<IPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IPacket packet) throws Exception {
        if (CloudAPI.getInstance().getConnectionRegistry().getConnection(channelHandlerContext) != null)
            CloudAPI.getInstance().getPacketRegistry().callIncoming(channelHandlerContext, packet);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        final String ip = ctx.channel().remoteAddress().toString().split(":")[0]
                .replaceFirst("/", "");
        if (Master.getInstance().getConfiguration().getCloudSettings().getDeniedConnectedAddress().contains(ip)) {
            ctx.channel().close();
        }

        if (!Master.getInstance().getConfiguration().getCloudSettings().getAllowedAddress().contains(ip)) {
            ctx.channel().close();
            if (!Master.getInstance().getConfiguration().getCloudSettings().getDeniedConnectedAddress().contains(ip)) {
                Master.getInstance().getConfiguration().getCloudSettings().getDeniedConnectedAddress().add(ip);
                Master.getInstance().updateConfig();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CloudAPI.getInstance().getConnectionRegistry().registerConnection(new Connection(null, null, null, ctx));
        System.out.println(MessageFormat.format("Channel [address={0}] connecting...",
                ctx.channel().remoteAddress().toString().replaceFirst("/", "")));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx) != null) {
            System.out.println(MessageFormat.format("Channel [address={0}] disconnected!",
                    ctx.channel().remoteAddress().toString().replaceFirst("/", "")));

            final Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection(ctx);
            UUID uniqueId = connection.getUniqueId();
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

            System.out.println(MessageFormat.format(connection.getType().getDisplayName()
                            + " [connectionId={0}#{1}/address={2}] logged out.", connection.getName(), uniqueId,
                    address.toString().replaceFirst("/", "")));

            CloudAPI.getInstance().getConnectionRegistry().unregisterConnection(connection);
            CloudAPI.getInstance().getClientRegistry().unregisterClient(CloudAPI.getInstance().getClientRegistry().getClient(connection));

            if (connection.getType() != null) {
                switch (connection.getType()) {
                    case SLAVE -> CloudAPI.getInstance().getResourceRegistry().unregisterResource(CloudAPI.getInstance().getResourceRegistry()
                            .getResource(connection.getName()));
                    case PROXY_SERVER -> {
                        final ProxyServer proxy = CloudAPI.getInstance().getServerRegistry().getServer(connection.getName(),
                                ProxyServer.class);
                        Master.getInstance().getCloudFlareManager().unregisterProxy(proxy);
                        CloudAPI.getInstance().getServerRegistry().unregisterServer(proxy);
                    }
                    case MINECRAFT_SERVER -> {
                        final MinecraftServer server = CloudAPI.getInstance().getServerRegistry().getServer(
                                connection.getName(), MinecraftServer.class);
                        if (!CloudAPI.getInstance().getServerRegistry().getAll().isEmpty()) {
                            CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry().getTemplate("Proxy"))
                                    .forEach(s -> CloudAPI.getInstance().getConnectionRegistry().getConnection(s.getName()).getChannel()
                                            .writeAndFlush(new PacketPlayOutUnregisterServer(connection.getName())));
                        }
                        CloudAPI.getInstance().getServerRegistry().unregisterServer(server);
                    }
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { }
}
