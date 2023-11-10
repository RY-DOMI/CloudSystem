package net.labormc.cloudapi.network.protocol.packets.template;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class PacketPlayOutUpdateModule implements IPacket {

    private String name;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.name = input.readUTF();
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(this.name);
    }
}
