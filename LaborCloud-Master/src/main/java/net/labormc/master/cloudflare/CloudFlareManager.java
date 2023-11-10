package net.labormc.master.cloudflare;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.document.AbstractDocument;
import net.labormc.cloudapi.network.client.Client;
import net.labormc.cloudapi.network.client.types.ProxyServer;
import net.labormc.master.Master;
import net.labormc.master.cloudflare.config.CloudFlareConfig;
import net.labormc.master.cloudflare.record.DnsRecord;
import net.labormc.master.cloudflare.record.DnsRecordId;
import net.labormc.master.cloudflare.record.DnsRecordType;
import net.labormc.master.cloudflare.record.SrvRecord;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudFlareManager {

    private final String base_url = "https://api.cloudflare.com/client/v4/";

    private final Map<String, PostResponse> records = new ConcurrentHashMap<>();

    public void init() {
        Master.getInstance().getConfiguration().getNetworkSettings().getSlaveList().forEach(slave ->
                Master.getInstance().getConfiguration().getCloudSettings().getCloudFlareList().forEach(cloudflare -> {
            final String host = slave.getHostName();
            final DnsRecord record = new DnsRecord(DnsRecordType.A.name(), slave.getName() + "."
                    + cloudflare.getDomainName(), host, 1, false, CloudAPI.getInstance().document().json());

            final PostResponse postResponse = this.createRecord(cloudflare, record);
            this.records.put(slave.getName(), postResponse);
        }));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.records.values().forEach(this::deleteRecord);
        }));
    }

    public void registerProxy(ProxyServer proxy) {
        Master.getInstance().getConfiguration().getCloudSettings().getCloudFlareList().forEach(config -> {
            final Client client = CloudAPI.getInstance().getClientRegistry().getClient(proxy.getSlaveUniqueId());
            SrvRecord srvRecord = new SrvRecord(
                    "_minecraft._tcp." + config.getDomainName(),
                    "SRV 1 1 " + proxy.getAddress().getPort() + " " + client.getUniqueId() + "."
                            + config.getDomainName(),
                    "_minecraft",
                    "_tcp",
                    config.getDomainName(),
                    1,
                    1,
                    proxy.getAddress().getPort(),
                    client.getName() + "." + config.getDomainName());

            if (!this.records.containsKey(proxy.getName())) {
                final PostResponse postResponse = this.createRecord(config, srvRecord);
                this.records.put(proxy.getName(), postResponse);
            }
        });
    }

    public void unregisterProxy(ProxyServer proxy) {
        if (!this.records.containsKey(proxy.getName()))
            return;

        final PostResponse response = this.records.get(proxy.getName());
        if (response != null) {
            this.records.remove(proxy.getName());
            this.deleteRecord(response);
        }
    }

    public void getRecords(DnsRecordType type, Consumer<List<DnsRecordId>> consumer) {
        Master.getInstance().getConfiguration().getCloudSettings().getCloudFlareList().forEach(config -> {
            try {
                final List<DnsRecordId> dnsRecords = new LinkedList<>();

                final HttpURLConnection httpPost = (HttpURLConnection) new URL(base_url + "zones/"
                        + config.getZoneId() + "/dns_records?" + type.name() + "&page=1&per_page=20&order=type&direction=desc&match=all").openConnection();

                httpPost.setRequestMethod("GET");
                httpPost.setRequestProperty("X-Auth-Email", config.getEmail());
                httpPost.setRequestProperty("X-Auth-Key", config.getAuthKey());
                httpPost.setRequestProperty("Accept", "application/json");
                httpPost.setRequestProperty("Content-Type", "application/json");
                httpPost.setDoOutput(true);
                httpPost.connect();

                try (InputStream inputStream = httpPost.getInputStream()) {
                    JsonObject jsonObject = toJsonInput(inputStream);

                    if (jsonObject.get("success").getAsBoolean())
                        for (Object o : jsonObject.getAsJsonArray("result")) {
                            final AbstractDocument document = CloudAPI.getInstance().document(
                                    CloudAPI.getInstance().getGson().fromJson(CloudAPI.getInstance().getGson().toJson(o),
                                    JSONObject.class));

                            dnsRecords.add(new DnsRecordId(document.getString("id"),
                                    document.getString("type"),
                                    document.getString("name"),
                                    document.getString("content"),
                                    document.getInt("ttl"),
                                    document.getBoolean("proxied"),
                                    document.get("data", JSONObject.class)));
                        }
                    else
                        throw new Exception("Failed to read all records " + jsonObject);
                    httpPost.disconnect();
                    consumer.accept(dnsRecords);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PostResponse createRecord(CloudFlareConfig cloudFlareConfig, DnsRecord record) {
        try {
            final HttpURLConnection httpPost = (HttpURLConnection) new URL(base_url + "zones/"
                    + cloudFlareConfig.getZoneId() + "/dns_records").openConnection();
            final String values = CloudAPI.getInstance().getGson().toJson(record);

            httpPost.setRequestMethod("POST");
            httpPost.setRequestProperty("X-Auth-Email", cloudFlareConfig.getEmail());
            httpPost.setRequestProperty("X-Auth-Key", cloudFlareConfig.getAuthKey());
            httpPost.setRequestProperty("Content-Length", values.getBytes().length + "");
            httpPost.setRequestProperty("Accept", "application/json");
            httpPost.setRequestProperty("Content-Type", "application/json");
            httpPost.setDoOutput(true);
            httpPost.connect();
            try (final DataOutputStream dataOutputStream = new DataOutputStream(httpPost.getOutputStream())) {
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
            }
            try (InputStream inputStream = httpPost.getInputStream()) {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean())
                    System.out.println("DNSRecord (" + record.getName() + "/" + record.getType()
                            + ") was created");
                else
                    throw new Exception("Failed to create DNSRecord " + jsonObject);
                httpPost.disconnect();
                return new PostResponse(cloudFlareConfig, record, jsonObject.get("result").getAsJsonObject().get("id")
                        .getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteRecord(PostResponse postResponse) {
        try {
            final HttpURLConnection delete = (HttpURLConnection) new URL(base_url + "zones/"
                    + postResponse.getCloudFlareConfig().getZoneId() + "/dns_records/" + postResponse.getId()).openConnection();
            delete.setRequestMethod("DELETE");
            delete.setRequestProperty("X-Auth-Email", postResponse.getCloudFlareConfig().getEmail());
            delete.setRequestProperty("X-Auth-Key", postResponse.getCloudFlareConfig().getAuthKey());
            delete.setRequestProperty("Accept", "application/json");
            delete.setRequestProperty("Content-Type", "application/json");
            delete.connect();
            try (InputStream inputStream = delete.getInputStream()) {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean())
                    System.out.println("DNSRecord (id=" + postResponse.getId() + ") was removed");
            }
            delete.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JsonObject toJsonInput(InputStream inputStream) {
        final StringBuilder stringBuilder = new StringBuilder();
        String input;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            while ((input = bufferedReader.readLine()) != null)
                stringBuilder.append(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonParser.parseString(stringBuilder.substring(0)).getAsJsonObject();
    }

}
