package net.labormc.cloudapi.network.protocol.packets.server;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.server.game.GameStates;

import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayOutUpdateSign implements IPacket {

    private String serverName;

    private GameStates gameState;

    private byte blockColor;

    private int onlinePlayers;
    private int maxPlayers;

    private double x;
    private double y;
    private double z;

    private String[] lines;

    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.serverName = input.readUTF();
        this.gameState = GameStates.valueOf(input.readUTF());
        this.blockColor = input.readByte();
        this.onlinePlayers = input.readInt();
        this.maxPlayers = input.readInt();
        this.lines = input.readUTF().split(", ");
        this.x = input.readDouble();
        this.y = input.readDouble();
        this.z = input.readDouble();
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(serverName);
        output.writeUTF(gameState.name());
        output.writeByte(blockColor);
        output.writeInt(onlinePlayers);
        output.writeInt(maxPlayers);
        output.writeUTF(Arrays.toString(lines).replace("[", "").replace("]", ""));
        output.writeDouble(x);
        output.writeDouble(y);
        output.writeDouble(z);
    }
}
