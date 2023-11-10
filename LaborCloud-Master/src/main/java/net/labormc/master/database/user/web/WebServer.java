package net.labormc.master.database.user.web;

import lombok.RequiredArgsConstructor;
import net.labormc.master.database.user.web.handler.RouteHandler;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.*;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@RequiredArgsConstructor
public class WebServer implements Runnable {

    private final WebConfiguration configuration;

    @Override
    public void run() {
        port(this.configuration.getPort());

        before("/*", (request, response) -> {
            final String path = request.pathInfo();
            if (path.endsWith("/*")) {
                response.redirect(path.substring(0, path.length() - 1), HttpStatus.MOVED_PERMANENTLY_301);
            }
        });

        before("/*", (request, response) -> {
            if (request.headers("X-Auth-Token") == null) {
                halt(HttpStatus.UNAUTHORIZED_401);
            } else if (request.headers("X-Auth-Token") != null && !request.headers("X-Auth-Token")
                    .equals(this.configuration.getToken())) {
                halt(HttpStatus.UNAUTHORIZED_401);
            }
        });

        this.initHandlers();
        System.out.println("Web-Server listen on :" + this.configuration.getPort() + this.configuration.getBase_path());
        System.out.println(" - Registered all Web-Handlers.");
    }

    private void initHandlers() {
        path(this.configuration.getBase_path(), () -> new RouteHandler().route());
    }
}
