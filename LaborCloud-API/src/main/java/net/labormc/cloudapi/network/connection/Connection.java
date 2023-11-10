package net.labormc.cloudapi.network.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class Connection {

    private UUID uniqueId;
    private String name;

    private ConnectionTypes type;
    private ChannelHandlerContext channel;
}
