package net.labormc.master.database.user.web.handler;

import net.labormc.master.database.user.web.WebRouteHandler;

import static spark.Spark.get;
/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class RouteHandler implements WebRouteHandler {

    @Override
    public void route() {
        get("/deploy", new DeployRoute());
    }
}
