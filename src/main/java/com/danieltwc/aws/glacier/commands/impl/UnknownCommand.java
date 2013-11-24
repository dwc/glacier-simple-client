package com.danieltwc.aws.glacier.commands.impl;

import com.danieltwc.aws.glacier.commands.GeneralCommand;

public class UnknownCommand extends GeneralCommand {
    public void run() throws Exception {
        super.run();
        throw new Exception("Unknown command");
    }
}
