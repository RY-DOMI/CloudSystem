package net.labormc.cloudapi.network.protocol.packets.api;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.network.protocol.IPacket;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayInAPIRequest implements IPacket {

    private UUID uniqueId;
    private APIRequestTypes type;

    private String[] args;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.uniqueId = UUID.fromString(input.readUTF());
        this.type = APIRequestTypes.valueOf(input.readUTF());
        this.args = input.readUTF().split(", ");
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(this.uniqueId.toString());
        output.writeUTF(this.type.name());
        output.writeUTF(Arrays.toString(this.args));

    }
}
