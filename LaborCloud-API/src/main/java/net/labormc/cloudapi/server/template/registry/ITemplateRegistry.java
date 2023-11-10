package net.labormc.cloudapi.server.template.registry;

import net.labormc.cloudapi.server.template.Template;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface ITemplateRegistry {

    void registerTemplate(Template template);
    void unregisterTemplate(Template template);

    Template getTemplate(String name);

    List<Template> getAll();
}
