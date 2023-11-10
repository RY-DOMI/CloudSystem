package net.labormc.cloud.impl.network.protocol.registry;

import com.google.common.collect.HashBasedTable;
import io.netty.channel.ChannelHandlerContext;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.network.protocol.listener.IPacketHandler;
import net.labormc.cloudapi.network.protocol.listener.IPacketListener;
import net.labormc.cloudapi.network.protocol.registry.IPacketRegistry;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketRegistryImpl implements IPacketRegistry {

    private final Map<String, IPacket> packetMap = new LinkedHashMap<>();
    private final HashBasedTable<Class<?>, Object, Method> packetListenerTable = HashBasedTable.create();

    @Override
    public IPacketRegistry registerPacket(IPacket packet) {
        if (!this.packetMap.containsKey(packet.getClass().getSimpleName()))
            this.packetMap.put(packet.getClass().getSimpleName(), packet);
        return this;
    }

    @Override
    public IPacketRegistry registerListener(IPacketListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(IPacketHandler.class)) continue;
            this.packetListenerTable.put(method.getParameterTypes()[0], listener, method);
        }
        return this;
    }

    @Override
    public void unregisterPacket(IPacket packet) {
        this.packetMap.remove(this.getName(packet));
    }

    @Override
    public void unregisterListener(IPacketListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(IPacketHandler.class)) continue;
            this.packetListenerTable.remove(method.getParameterTypes()[0], listener);
        }
    }

    @Override
    public IPacket getPacket(String name) {
        return this.packetMap.get(name);
    }

    @Override
    public String getName(IPacket packet) {
        return Objects.requireNonNull(this.packetMap.entrySet().stream()
                .filter(entry -> entry.getValue().getClass().getSimpleName().equals(packet.getClass().getSimpleName()))
                .findFirst()
                .orElse(null)).getKey();
    }

    @Override
    public void callIncoming(ChannelHandlerContext channel, IPacket packet) {
        if (!this.packetListenerTable.containsRow(packet.getClass()))
            return;

        this.packetListenerTable.row(packet.getClass()).forEach((listener, method) -> {
            try {
                method.invoke(listener, packet, channel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
