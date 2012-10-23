package com.danieltwc.aws.glacier.commands.impl;

import com.danieltwc.aws.glacier.commands.GlacierCommand;

public class UnknownCommand extends GlacierCommand {
    public void run() throws Exception {
        throw new IllegalArgumentException("Unknown command");
    }
}
