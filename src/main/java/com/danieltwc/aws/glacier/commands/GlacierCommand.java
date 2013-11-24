package com.danieltwc.aws.glacier.commands;

import java.io.PrintStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;

public abstract class GlacierCommand implements Command {
    protected PrintStream out;
    protected List<String> args;
    protected AWSCredentials credentials;
    protected AmazonGlacierClient client;

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void setCredentials(AWSCredentials credentials) {
        this.credentials = credentials;
    }

    public void setClient(AmazonGlacierClient client) {
        this.client = client;
    }

    public void run() throws Exception {
    }
}
