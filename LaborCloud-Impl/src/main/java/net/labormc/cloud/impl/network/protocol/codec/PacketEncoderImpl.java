package net.labormc.cloud.impl.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.network.protocol.IPacket;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PacketEncoderImpl extends MessageToByteEncoder<IPacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacket packet, ByteBuf byteBuf) throws Exception {
        final String name = CloudAPI.getInstance().getPacketRegistry().getName(packet);
        final ByteBufOutputStream output = new ByteBufOutputStream(byteBuf);

        output.writeUTF(name);
        packet.write(output);
    }
}
