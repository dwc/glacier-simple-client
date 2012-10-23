package com.danieltwc.aws.glacier.commands;

public interface Command {
    public void setArgs(String... args);
    public void run() throws Exception;
}
