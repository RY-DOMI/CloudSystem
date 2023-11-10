package net.labormc.master.server;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.client.types.MinecraftServer;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.cloudapi.network.client.types.Slave;
import net.labormc.cloudapi.network.connection.Connection;
import net.labormc.cloudapi.network.protocol.packets.resource.PacketPlayOutUpdateResource;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayInDeleteServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutStartProxy;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutStartServer;
import net.labormc.cloudapi.network.protocol.packets.server.PacketPlayOutStopServer;
import net.labormc.cloudapi.resource.Resource;
import net.labormc.cloudapi.server.CloudServer;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.server.template.enums.TemplateTypes;
import net.labormc.master.Master;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ServerQueue extends Thread {

    @Override
    public void run() {
        new Timer("server-queue").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CloudAPI.getInstance().getTemplateRegistry().getAll().forEach(template -> {
                    final AtomicInteger count = new AtomicInteger(0);

                    Master.getInstance().getQueuedServerList().forEach(server -> {
                        if (server.getTemplate() == template)
                            count.addAndGet(1);
                    });

                    final int size = CloudAPI.getInstance().getServerRegistry().getAll(template).size() + count.get();
                    if (size < template.getMinOnline())
                        for (int i = 0; i < (template.getMinOnline() - size); i++)
                            addToQueue(new QueueServer(UUID.randomUUID(), template));
                });

                if (!CloudAPI.getInstance().getClientRegistry().getAll(Slave.class).isEmpty()
                        && !CloudAPI.getInstance().getResourceRegistry().getAll().isEmpty() && !Master.getInstance()
                        .getQueuedServerList().isEmpty()) {
                    final QueueServer server = Master.getInstance().getQueuedServerList().get(0);
                    Master.getInstance().getQueuedServerList().remove(server);
                    if (server != null)
                        startServer(server);

                }
            }
        }, 500, 500);
    }

    public void addToQueue(QueueServer server) {
        Master.getInstance().getQueuedServerList().add(server);
    }

    private void startServer(QueueServer server) {
        try {
            final String name = this.getName(server);
            final Template template = server.getTemplate();
            int port = 0;
            if (template.getType() == TemplateTypes.PROXY)
                port = this.getProxyPort();

            final Slave slave = ((template.getType() == TemplateTypes.PROXY && name.equalsIgnoreCase("Proxy-1"))
                    ? this.getBestSlave(template.getMaxMemory()) : (Slave) CloudAPI.getInstance().getClientRegistry().getClient("Slave-1"));

            CloudAPI.getInstance().getServerRegistry().getServer(name).setSlaveUniqueId(slave.getUniqueId());
            final int usedRam = CloudAPI.getInstance().getResourceRegistry().getResource(slave.getName())
                    .getUsedRam();

            CloudAPI.getInstance().getResourceRegistry().getResource(slave.getName()).setUsedRam(usedRam
                    + template.getMaxMemory());

            final Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection(slave.getName());
            connection.getChannel().writeAndFlush(new PacketPlayOutUpdateResource(CloudAPI.getInstance()
                    .getResourceRegistry().getResource(slave.getName())));
            connection.getChannel().writeAndFlush((template.getType() == TemplateTypes.SERVER
                    ? new PacketPlayOutStartServer(server.getUniqueId(), name, template)
                    : new PacketPlayOutStartProxy(server.getUniqueId(), name, port, template)));

            System.out.println(MessageFormat.format("{0} [serviceId={1}#{2}/slave={3}/template={4}] started!",
                    template.getType().getDisplayName(), name, server.getUniqueId(), slave.getName(), template.getName()));
        } catch (Exception ignored) {
            this.addToQueue(server);
        }
    }

    public void stopServer(String name) {
        final CloudServer server = CloudAPI.getInstance().getServerRegistry().getServer(name);
        final Connection connection = CloudAPI.getInstance().getConnectionRegistry().getConnection(server.getSlaveUniqueId());
        final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(server.getTemplateName());

        connection.getChannel().writeAndFlush(new PacketPlayOutStopServer(name, template));

        CloudAPI.getInstance().getServerRegistry().getAll().forEach((s) -> {
            CloudAPI.getInstance().getConnectionRegistry().getConnection(s.getName())
                    .getChannel().writeAndFlush(new PacketPlayInDeleteServer(name, server.getTemplateName()));
        });

        System.out.println(MessageFormat.format("{0} [serviceId={1}#{2}/slave={3}] stopped!", template.getType().getDisplayName(),
                name, server.getUniqueId(), connection.getName()));

        Master.getInstance().getSignManager().clearSign(server);
    }

    private Slave getBestSlave(int maxMemory) {
        final Map<Slave, Integer> slaveMap = new LinkedHashMap<>();

        CloudAPI.getInstance().getClientRegistry().getAll(Slave.class).forEach(sl -> {
            final Resource resource = CloudAPI.getInstance().getResourceRegistry().getResource(sl.getName());

            if ((resource.getCpuAverage() <= 90.0) && ((resource.getUsedRam() + maxMemory) <= resource.getMaxRam()))
                slaveMap.put((Slave) sl, (int) resource.getCpuAverage());
        });

        return slaveMap.entrySet().stream().filter(entry -> Collections.min(slaveMap.values()).intValue() == entry.getValue())
                .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    private String getName(QueueServer server) {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            final String name = server.getTemplate().getName() + "-" + i;
            if (CloudAPI.getInstance().getServerRegistry().getServer(name) == null) {
                switch (server.getTemplate().getType()) {

                    case PROXY:
                        CloudAPI.getInstance().getServerRegistry().registerServer(new ProxyServer(server.getUniqueId(),
                                name, null, 0, 0, server.getTemplate().getName(), null));
                        break;

                    case SERVER:
                        CloudAPI.getInstance().getServerRegistry().registerServer(new MinecraftServer(server.getUniqueId(), null, null,
                                0, 0, server.getTemplate().getName(), null, "", null));
                        break;

                }
                return name;
            }
        }
        return null;
    }

    private int getProxyPort() {
        int port = 25565;
        final List<CloudServer> all = CloudAPI.getInstance().getServerRegistry().getAll(CloudAPI.getInstance().getTemplateRegistry()
                .getTemplate("Proxy"));

        for (int i = 0; i < Integer.MAX_VALUE; i++)
            if (this.proxyPortIsAvailable(port, all))
                break;
            else
                port++;
        return port;
    }

    private boolean proxyPortIsAvailable(int port, List<CloudServer> all) {
        final List<Integer> list = new LinkedList<>();
        all.forEach(server -> list.add(server.getPort()));

        return !list.contains(port);
    }
}
