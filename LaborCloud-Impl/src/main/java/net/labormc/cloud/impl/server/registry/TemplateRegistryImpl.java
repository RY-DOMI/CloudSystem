package net.labormc.cloud.impl.server.registry;

import net.labormc.cloudapi.server.template.Template;
import net.labormc.cloudapi.server.template.registry.ITemplateRegistry;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class TemplateRegistryImpl implements ITemplateRegistry {

    private final List<Template> templateList = new LinkedList<>();

    @Override
    public void registerTemplate(Template template) {
        if (! this.templateList.contains(template)) {
            this.templateList.add(template);
        }
    }

    @Override
    public void unregisterTemplate(Template template) {
        this.templateList.remove(template);
    }

    @Override
    public Template getTemplate(String name) {
        return this.templateList.stream()
                .filter(template -> template.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Template> getAll() {
        return this.templateList;
    }
}
