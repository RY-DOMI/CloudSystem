package net.labormc.cloud.impl.logging;

import net.labormc.cloud.impl.logging.formatter.ConsoleLoggingFormatter;
import net.labormc.cloud.impl.logging.formatter.FileLoggingFormatter;
import net.labormc.cloudapi.logging.AbstractCloudLogger;
import org.apache.commons.io.FileUtils;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudLoggerImpl extends AbstractCloudLogger {

    public CloudLoggerImpl() throws IOException {
        super(new ConsoleLoggingFormatter());
    }

    @Override
    protected void init() throws IOException {
        this.setLevel(Level.ALL);

        this.getReader().setExpandEvents(false);

        final File logsFile = new File("logs/");
        if (!logsFile.exists())
            logsFile.mkdirs();

        final FileHandler handler = new FileHandler(logsFile.getCanonicalPath() + "/latest.log", 1000 * 1024, 8, true);
        handler.setFormatter(new FileLoggingFormatter());
        this.addHandler(handler);

        final CloudLoggerHandler loggingHandler = new CloudLoggerHandler(this);
        loggingHandler.setFormatter(this.getFormatter());
        loggingHandler.setLevel(Level.INFO);
        this.addHandler(loggingHandler);

        System.setOut(new PrintStream(new CloudLoggerOutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new CloudLoggerOutputStream(this, Level.SEVERE), true));
    }

    @Override
    public void shutdown() {
        AnsiConsole.systemUninstall();
        FileUtils.deleteQuietly(new File("logs/latest.log.0.lck"));
    }
}
