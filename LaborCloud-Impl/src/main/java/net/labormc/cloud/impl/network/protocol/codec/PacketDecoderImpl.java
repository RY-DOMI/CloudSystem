package net.labormc.cloud.impl.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.IPacket;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketDecoderImpl extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final ByteBufInputStream input = new ByteBufInputStream(byteBuf);
        final String name = input.readUTF();
        final IPacket packet = CloudAPI.getInstance().getPacketRegistry().getPacket(name);

        if (packet == null)
            System.err.println("Couldn't find packet with name " + name + "!");
        else {
            packet.read(input);
            list.add(packet);
        }
    }
}
