package net.labormc.cloudapi.network.protocol.packets.server;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.IPacket;
import net.labormc.cloudapi.server.template.Template;

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
public class PacketPlayOutStartServer implements IPacket {

    private UUID uniqueId;
    private String name;
    
    private Template template;
    
    @Override
    public void read(ByteBufInputStream input) throws IOException {
        this.uniqueId = UUID.fromString(input.readUTF());
        this.name = input.readUTF();
        
        this.template = CloudAPI.getInstance().getGson().fromJson(input.readUTF(), Template.class);
    }

    @Override
    public void write(ByteBufOutputStream output) throws IOException {
        output.writeUTF(this.uniqueId.toString());
        output.writeUTF(this.name);
        
        output.writeUTF(CloudAPI.getInstance().getGson().toJson(this.template));
    }

}
