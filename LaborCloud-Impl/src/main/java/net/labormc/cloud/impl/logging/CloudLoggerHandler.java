package net.labormc.cloud.impl.logging;

import jline.console.ConsoleReader;
import net.labormc.cloudapi.logging.AbstractCloudLogger;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudLoggerHandler extends Handler {

    private final AbstractCloudLogger logger;

    public CloudLoggerHandler(AbstractCloudLogger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record))
            this.handle(this.logger.getFormatter().format(record));
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }

    private void handle(String message) {
        try {
            final ConsoleReader reader = this.logger.getReader();

            reader.print(ConsoleReader.RESET_LINE + message);
            reader.drawLine();
            reader.flush();
        } catch (IOException ex) {}
    }
}
