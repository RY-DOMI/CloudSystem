package net.labormc.cloud.impl.message;

import net.labormc.cloudapi.message.Message;
import net.labormc.cloudapi.message.registry.IMessageRegistry;
import net.labormc.cloudapi.user.enums.LanguageTypes;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class MessageRegistryImpl implements IMessageRegistry {

    private final List<Message> messageList = new LinkedList<>();

    @Override
    public void registerMessage(Message message) {
        if (!messageList.contains(message))
            messageList.add(message);
    }

    @Override
    public void unregisterMessage(Message message) {
        messageList.remove(message);
    }

    @Override
    public String getMessage(String name, LanguageTypes languageType) {
        return Objects.requireNonNull(this.messageList.stream()
                        .filter(message -> Objects.equals(message.getName(), name))
                        .findFirst()
                        .orElse(null))
                .getMessages().get(languageType);
    }

    @Override
    public List<Message> getAll() {
        return this.messageList;
    }
}
