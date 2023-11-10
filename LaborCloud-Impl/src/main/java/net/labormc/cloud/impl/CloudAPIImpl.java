package net.labormc.cloud.impl;

import com.google.gson.GsonBuilder;
import net.labormc.cloud.impl.cloudplayer.registry.CloudPlayerImpl;
import net.labormc.cloud.impl.command.registry.CloudCommandRegistryImpl;
import net.labormc.cloud.impl.document.Document;
import net.labormc.cloud.impl.message.MessageRegistryImpl;
import net.labormc.cloud.impl.network.client.registry.ClientRegistryImpl;
import net.labormc.cloud.impl.network.connection.registry.ConnectionRegistryImpl;
import net.labormc.cloud.impl.network.protocol.registry.PacketRegistryImpl;
import net.labormc.cloud.impl.resource.registry.ResourceRegistryImpl;
import net.labormc.cloud.impl.server.registry.ServerRegistryImpl;
import net.labormc.cloud.impl.server.registry.TemplateRegistryImpl;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudAPIImpl extends CloudAPI {

    public CloudAPIImpl() {
        init();
    }

    @Override
    protected void init() {
        try {
            this.setClientRegistry(new ClientRegistryImpl());
            this.setConnectionRegistry(new ConnectionRegistryImpl());
            this.setPacketRegistry(new PacketRegistryImpl());
            this.setCloudCommandRegistry(new CloudCommandRegistryImpl());
            this.setResourceRegistry(new ResourceRegistryImpl());
            this.setTemplateRegistry(new TemplateRegistryImpl());
            this.setServerRegistry(new ServerRegistryImpl());
            this.setCloudPlayerRegistry(new CloudPlayerImpl());
            this.setMessageRegistry(new MessageRegistryImpl());

            this.setGson(new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create());
        } catch (Exception ex) {
            System.err.println("Couldn't load the CloudAPI!");
            Logger.getLogger(CloudAPIImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public AbstractDocument document() {
        return new Document();
    }

    @Override
    public AbstractDocument document(JSONObject object) {
        return new Document(object);
    }
}
