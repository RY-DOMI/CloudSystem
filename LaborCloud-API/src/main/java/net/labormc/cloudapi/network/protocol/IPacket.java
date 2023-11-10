package net.labormc.cloudapi.network.protocol;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IPacket {

    void read(ByteBufInputStream input) throws IOException;
    void write(ByteBufOutputStream output) throws IOException;
}
