package net.labormc.cloudapi.network.protocol.packets.resource;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.resource.Resource;

import java.io.IOException;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayInResourceInfo implements IPacket {

    private Resource resource;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.resource = CloudAPI.getInstance().getGson().fromJson(input.readUTF(), Resource.class);
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(CloudAPI.getInstance().getGson().toJson(this.resource));
    }
}
