package net.labormc.cloud.impl.logging.formatter;

import net.labormc.cloudapi.logging.ConsoleColors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class ConsoleLoggingFormatter extends Formatter {


    private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        final StringBuilder formatted = new StringBuilder();

        formatted.append(ConsoleColors.BLACK_BOLD);
        formatted.append("[");
        formatted.append(ConsoleColors.WHITE_BOLD);
        formatted.append(this.format.format(record.getMillis()));
        formatted.append(ConsoleColors.BLACK_BOLD);
        formatted.append("]");
        formatted.append(" ");
        formatted.append(this.getLevelColor(record.getLevel()));
        formatted.append(record.getLevel().getLocalizedName());
        formatted.append(ConsoleColors.BLACK_BOLD);
        formatted.append(": ");
        formatted.append((record.getLevel() == Level.SEVERE ? this.getLevelColor(record.getLevel()) : ConsoleColors.WHITE));
        formatted.append(formatMessage(record));

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            formatted.append(writer);
        }

        return formatted.toString();
    }

    private String getLevelColor(Level level) {
        switch(level.getName()) {
            case "INFO":
                return ConsoleColors.YELLOW_BOLD;
            case "WARNING":
                return ConsoleColors.YELLOW;
            case "SEVERE":
                return ConsoleColors.RED;
            default:
                return ConsoleColors.WHITE;
        }
    }
}
