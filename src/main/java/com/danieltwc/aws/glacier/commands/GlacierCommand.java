package com.danieltwc.aws.glacier.commands;

import java.io.PrintStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;

public abstract class GlacierCommand implements Command {
    protected PrintStream out;
    protected String[] args;
    protected AWSCredentials credentials;
    protected AmazonGlacierClient client;
    protected String vaultName;

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void setArgs(String... args) {
        this.args = args;
    }

    public void setCredentials(AWSCredentials credentials) {
        this.credentials = credentials;
    }

    public void setClient(AmazonGlacierClient client) {
        this.client = client;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }
}
