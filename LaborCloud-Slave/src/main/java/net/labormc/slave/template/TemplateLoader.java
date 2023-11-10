package net.labormc.slave.template;

import net.labormc.cloudapi.CloudAPI;
import net.labormc.cloudapi.server.template.Template;
import net.labormc.slave.Slave;
import net.labormc.slave.web.WebConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class TemplateLoader {

    public void downloadTemplates() {
        final HttpURLConnection connection = this.openConnection("templates", null);
        System.out.println("Downloading templates...");

        final Throwable throwable = this.readData(connection, Paths.get("downloads",
                "templates.zip").toString());
        if (throwable == null) {
            System.out.println("Successfully downloaded templates!");
            this.unzip(new File("downloads/templates.zip"),
                    new File("."));
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    public void downloadPlugins() {
        final HttpURLConnection connection = this.openConnection("plugins", null);
        System.out.println("Downloading plugins...");

        final Throwable throwable = this.readData(connection, "downloads/plugins.zip");
        if (throwable == null) {
            System.out.println("Successfully downloaded plugins!");
            this.unzip(new File("downloads/plugins.zip"),
                    new File("."));
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    public void downloadModules() {
        final HttpURLConnection connection = this.openConnection("modules", null);
        System.out.println("Downloading modules...");

        final Throwable throwable = this.readData(connection, "downloads/modules.zip");
        if (throwable == null) {
            System.out.println("Successfully downloaded modules!");
            this.unzip(new File("downloads/modules.zip"),
                    new File("."));
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    public void downloadTemplate(String name) {
        final HttpURLConnection connection = this.openConnection("template", name);
        System.out.println("Downloading template \"" + name + "\"...");

        final Throwable throwable = this.readData(connection, "downloads/" + name + ".zip");
        if (throwable == null) {
            this.unzip(new File("downloads/" + name + ".zip"),
                    new File("/templates/"));

            final Template template = CloudAPI.getInstance().getTemplateRegistry().getTemplate(name);
            template.getPluginList().forEach(this::downloadPlugin);
            template.getModuleList().forEach(this::downloadModule);
            System.out.println("Successfully downloaded template \"" + name + "\"!");
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    public void downloadPlugin(String name) {
        final HttpURLConnection connection = this.openConnection("plugin", name);
        System.out.println("Downloading plugin \"" + name + "\"...");

        final Throwable throwable = this.readData(connection, "downloads/" + name + ".zip");
        if (throwable == null) {
            this.unzip(new File( "downloads/" + name + ".zip"),
                    new File("/plugins/"));
            System.out.println("Successfully downloaded plugin \"" + name + "\"!");
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    public void downloadModule(String name) {
        final HttpURLConnection connection = this.openConnection("module", name);
        System.out.println("Downloading module \"" + name + "\"...");

        final Throwable throwable = this.readData(connection, "downloads/" + name + ".zip");
        if (throwable == null) {
            this.unzip(new File("downloads/" + name + ".zip"),
                    new File("/modules/"));
            System.out.println("Successfully downloaded module \"" + name + "\"!");
        } else
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, throwable);
    }

    private HttpURLConnection openConnection(String type, String name) {
        final WebConfiguration web = Slave.getInstance().getConfiguration().getWebSettings();
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL("http://" + web.getHostName() + ":" + web.getPort()
                    + web.getBase_path() + "/deploy")
                    .openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            connection.setRequestProperty("X-Auth-Token", web.getToken());
            connection.setRequestProperty("type", type);
            if (name != null)
                connection.setRequestProperty("name", name);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            return connection;
        } catch (IOException ex) {
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private Throwable readData(HttpURLConnection connection, String filePath) {
        try {
            final InputStream inputStream = connection.getInputStream();
            final FileOutputStream outputStream = new FileOutputStream(
                    Paths.get(filePath).toFile());

            final byte[] buffer = new byte[2048];

            int length;

            while ((length = inputStream.read(buffer)) != - 1)
                outputStream.write(buffer, 0, length);
            outputStream.close();
            return null;
        } catch (IOException ex) {
            return ex;
        }
    }

    private void unzip(File file, File target) {
        try {
            final byte[] buffer = new byte[1024];
            if (!target.exists())
                target.mkdirs();

            final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file));

            while (true) {
                ZipEntry entry;
                while ((entry = inputStream.getNextEntry()) != null) {
                    final String fileName = entry.getName();
                    final File newFile = new File(target.getPath() + File.separator + fileName);
                    if (entry.isDirectory())
                        newFile.mkdir();
                    else {
                        new File(newFile.getParent()).mkdirs();
                        final FileOutputStream outputStream = new FileOutputStream(newFile);
                        Throwable throwable = null;

                        try {
                            int bytes;
                            try {
                                while ((bytes = inputStream.read(buffer)) > 0)
                                    outputStream.write(buffer, 0, bytes);
                            } catch (Throwable throwable1) {
                                throwable = throwable1;
                                throw throwable1;
                            }
                        } finally {
                            if (outputStream != null)
                                if (throwable != null)
                                    try {
                                        outputStream.close();
                                    } catch (Throwable var17) {
                                        throwable.addSuppressed(var17);
                                    }
                                else
                                    outputStream.close();

                        }
                    }
                }

                inputStream.closeEntry();
                inputStream.close();
                file.delete();
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
