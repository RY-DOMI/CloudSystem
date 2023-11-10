package net.labormc.cloudapi.message.registry;

import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.user.enums.LanguageTypes;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public interface IMessageRegistry {

    void registerMessage(Message message);
    void unregisterMessage(Message message);

    String getMessage(String name, LanguageTypes languageType);

    List<Message> getAll();

}
