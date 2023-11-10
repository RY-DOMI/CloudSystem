package net.labormc.cloudapi.logging;

import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
import java.util.logging.*;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@Getter
public abstract class AbstractCloudLogger extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final ConsoleReader reader;
    private final Formatter formatter;

    public AbstractCloudLogger(Formatter formatter) throws IOException {
        super("LaborCloud-Logging", null);
        this.reader = new ConsoleReader(System.in, System.out);
        this.formatter = formatter;

        try {
            this.init();
        } catch (IOException ex) {
            Logger.getLogger(AbstractCloudLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void init() throws IOException;

    public abstract void shutdown();
}
