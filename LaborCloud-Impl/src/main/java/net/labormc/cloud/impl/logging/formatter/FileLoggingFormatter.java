package net.labormc.cloud.impl.logging.formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class FileLoggingFormatter extends Formatter {

    private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        final StringBuilder formatted = new StringBuilder();

        formatted.append("[");
        formatted.append(this.format.format(record.getMillis()));
        formatted.append("]");
        formatted.append(" ");
        formatted.append(record.getLevel().getLocalizedName());
        formatted.append(": ");
        formatted.append(formatMessage(record));

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            formatted.append(writer);
        }

        return formatted.toString();
    }
}
