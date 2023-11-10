package net.labormc.cloudapi;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.cloudplayer.registry.ICloudPlayerRegistry;
import net.labormc.cloudapi.document.registry.ICloudCommandRegistry;
import net.labormc.cloudapi.document.AbstractDocument;
import net.labormc.cloudapi.logging.AbstractCloudLogger;
import net.labormc.cloudapi.message.registry.IMessageRegistry;
import net.labormc.cloudapi.network.client.registry.IClientRegistry;
import net.labormc.cloudapi.network.connection.registry.IConnectionRegistry;
import net.labormc.cloudapi.network.protocol.registry.IPacketRegistry;
import net.labormc.cloudapi.resource.registry.IResourceRegistry;
import net.labormc.cloudapi.server.registry.IServerRegistry;
import net.labormc.cloudapi.server.template.registry.ITemplateRegistry;
import org.json.simple.JSONObject;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */

@Getter
@Setter
public abstract class CloudAPI {

    @Getter
    @Setter
    private static CloudAPI instance;

    private AbstractCloudLogger cloudLogger;
    private IClientRegistry clientRegistry;
    private IConnectionRegistry connectionRegistry;
    private IPacketRegistry packetRegistry;
    private ICloudCommandRegistry cloudCommandRegistry;
    private IResourceRegistry resourceRegistry;
    private IServerRegistry serverRegistry;
    private ITemplateRegistry templateRegistry;
    private ICloudPlayerRegistry cloudPlayerRegistry;
    private IMessageRegistry messageRegistry;

    private Gson gson;

    protected abstract void init();

    public abstract AbstractDocument document();
    public abstract AbstractDocument document(JSONObject object);
}
