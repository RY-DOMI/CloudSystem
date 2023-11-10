package net.labormc.master.database.user.web.handler;

import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class DeployRoute implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (request.headers("type") == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return "null";
        }

        final String type = request.headers("type");
        switch (type.toLowerCase()) {

            case "templates":
                final File temp = new File("templates/");
                if (!temp.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File tempZip = new File("deploy/templates.zip");
                this.zip(temp, tempZip.toString());
                if (!temp.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=templates.zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(tempZip.toPath()));
                }
                return response.raw();

            case "plugins":
                final File plugin = new File("plugins/");
                if (!plugin.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File pluginsZip = new File("deploy/plugins.zip");
                this.zip(plugin, pluginsZip.toString());
                if (!plugin.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=plugins.zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(pluginsZip.toPath()));
                }
                return response.raw();

            case "modules":
                final File module = new File("modules/");
                if (!module.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File moduleZip = new File("deploy/modules.zip");
                this.zip(module, moduleZip.toString());
                if (!module.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=modules.zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(moduleZip.toPath()));
                }
                return response.raw();

            case "template":
                final String name = request.headers("name");
                final File temp1 = new File("templates/" + name);
                if (!temp1.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File tempZip1 = new File("deploy/" + name + ".zip");
                this.zip(temp1, tempZip1.toString());
                if (!temp1.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=" + name + ".zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(tempZip1.toPath()));
                }
                return response.raw();

            case "plugin":
                final String pluginName = request.headers("name");
                final File plugin1 = new File("plugins/" + pluginName + ".jar");
                if (!plugin1.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File pluginZip = new File("deploy/" + pluginName + ".zip");
                this.zip(plugin1, pluginZip.toString());
                if (!plugin1.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=" + pluginName + ".zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(pluginZip.toPath()));
                }
                return response.raw();

            case "module":
                final String moduleName = request.headers("name");
                final File module1 = new File("modules/" + moduleName + ".jar");
                if (!module1.exists()) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return "";
                }

                final File moduleZip1 = new File("deploy/" + moduleName + ".zip");
                this.zip(module1, moduleZip1.toString());
                if (!module1.exists()) {
                    response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    return "";
                }

                response.raw().setContentType("application/octet-stream");
                response.raw().setHeader("Content-Disposition", "attachment; filename=" + moduleName + ".zip");

                try (final BufferedOutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream())) {
                    outputStream.write(Files.readAllBytes(moduleZip1.toPath()));
                }
                return response.raw();

        }
        return "";
    }

    private void zip(File file, String name) {
        try {
            final FileOutputStream outputStream = new FileOutputStream(name);
            final ZipOutputStream zipOut = new ZipOutputStream(outputStream);

            final String fileName = file.getName();
            this.zipFile(file, fileName, zipOut);

            zipOut.close();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void zipFile(File file, String fileName, ZipOutputStream zipOut) throws IOException {
        if (file.isHidden())
            return;
        if (file.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = file.listFiles();
            for (File childFile : children)
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0)
            zipOut.write(bytes, 0, length);
        fis.close();
    }
}
