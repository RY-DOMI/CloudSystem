package net.labormc.cloudapi.network.protocol.packets.cloudplayer;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.cloudplayer.CloudPlayer;
import net.labormc.cloudapi.network.protocol.IPacket;

import java.io.IOException;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayInCloudPlayerConnected implements IPacket {

    private CloudPlayer cloudPlayer;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.cloudPlayer = CloudAPI.getInstance().getGson().fromJson(input.readUTF(), CloudPlayer.class);
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(CloudAPI.getInstance().getGson().toJson(this.cloudPlayer));
    }
}
