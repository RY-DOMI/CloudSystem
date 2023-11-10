package net.labormc.cloudapi.network.protocol.packets.server;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.IPacket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPlayOutRegisterServer implements IPacket {

    private Class clazz;
    private Object object;
    
    @Override
    public void read(ByteBufInputStream input) throws IOException {
        try {
            this.clazz = Class.forName(input.readUTF());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PacketPlayOutRegisterServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.object = CloudAPI.getInstance().getGson().fromJson(input.readUTF(), clazz);
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(this.clazz.getCanonicalName());
        output.writeUTF(CloudAPI.getInstance().getGson().toJson(this.object));
    }

}
