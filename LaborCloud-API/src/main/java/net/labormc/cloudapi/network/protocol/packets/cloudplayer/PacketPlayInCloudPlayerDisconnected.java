package net.labormc.cloudapi.network.protocol.packets.cloudplayer;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.network.protocol.IPacket;

import java.io.IOException;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayInCloudPlayerDisconnected implements IPacket {

    private UUID cloudPlayerUUID;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.cloudPlayerUUID = UUID.fromString(input.readUTF());
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(cloudPlayerUUID.toString());
    }
}
