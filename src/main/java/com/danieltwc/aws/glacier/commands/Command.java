package com.danieltwc.aws.glacier.commands;

import java.util.List;

public interface Command {
    public void setArgs(List<String> args);
    public void run() throws Exception;
}
