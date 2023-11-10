package net.labormc.cloud.impl.logging;

import net.labormc.cloudapi.logging.AbstractCloudLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class CloudLoggerOutputStream extends ByteArrayOutputStream {

    private final AbstractCloudLogger logger;
    private final Level level;

    public CloudLoggerOutputStream(AbstractCloudLogger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        final String content = toString(StandardCharsets.UTF_8.name());
        super.reset();
        if (!content.isEmpty() && !content.equals(this.logger.getSeparator()) && !content.contains("SLF4J"))
            this.logger.logp(this.level, "", "", content);
    }
}
