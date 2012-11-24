package com.danieltwc.aws.glacier;

import java.util.Arrays;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.glacier.AmazonGlacierClient;

import com.danieltwc.aws.HomeDirectoryAWSCredentialsProvider;
import com.danieltwc.aws.glacier.commands.GlacierCommand;
import com.danieltwc.aws.glacier.commands.impl.*;

public class App {
    static AWSCredentials credentials;
    static AmazonGlacierClient client;

    public static void main(String[] args) {
        loadCredentials();
        loadClient();
        runCommand(args);
    }

    public static void loadCredentials() {
        AWSCredentialsProvider provider = new AWSCredentialsProviderChain(
            new HomeDirectoryAWSCredentialsProvider(),
            new DefaultAWSCredentialsProviderChain()
        );

        credentials = provider.getCredentials();
    }

    public static void loadClient() {
        client = new AmazonGlacierClient(credentials);
    }

    public static void runCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("App <command> <vault> [...]");
            System.exit(1);
        }

        String command = args[0].toLowerCase();
        String vaultName = args[1];
        String[] commandArgs = (String[]) Arrays.copyOfRange(args, 2, args.length);

        try {
            GlacierCommand cmd = new UnknownCommand();

            if (command.equals("list")) {
            }
            else if (command.equals("upload")) {
                cmd = new UploadCommand();
            }
            else if (command.equals("delete")) {
                cmd = new DeleteCommand();
            }
            else if (command.equals("download")) {
                // TODO
            }
            else if (command.equals("inventory")) {
                cmd = new InventoryCommand();
            }

            cmd.setOut(System.out);
            cmd.setArgs(commandArgs);
            cmd.setClient(client);
            cmd.setCredentials(credentials);
            cmd.setVaultName(vaultName);

            cmd.run();
        }
        catch (Exception e) {
            System.err.println("Command [" + command + "] failed:");
            System.err.println(e);
            System.exit(1);
        }
    }
}
