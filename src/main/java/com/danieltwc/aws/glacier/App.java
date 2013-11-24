package com.danieltwc.aws.glacier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.glacier.AmazonGlacierClient;

import com.danieltwc.aws.HomeDirectoryAWSCredentialsProvider;
import com.danieltwc.aws.glacier.commands.GlacierCommand;
import com.danieltwc.aws.glacier.commands.GeneralCommand;
import com.danieltwc.aws.glacier.commands.VaultCommand;
import com.danieltwc.aws.glacier.commands.impl.*;

public class App {
    /** Command name and associated object to run that command */
    private static final Map<String, GlacierCommand> COMMANDS = new HashMap<String, GlacierCommand>();

    static AWSCredentials credentials;
    static AmazonGlacierClient client;

    public static void main(String[] rawArgs) {
        COMMANDS.put("list", new ListCommand());
        COMMANDS.put("inventory", new InventoryCommand());
        COMMANDS.put("upload", new UploadCommand());
        COMMANDS.put("download", new DownloadCommand());
        COMMANDS.put("delete", new DeleteCommand());
        COMMANDS.put("save-job-output", new SaveJobOutputCommand());

        List<String> args = new ArrayList<String>(Arrays.asList(rawArgs));

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

    public static void runCommand(List<String> args) {
        // Pull the command off the arguments
        if (args.size() < 1) {
            System.err.println(usage());
            System.exit(1);
        }

        String cmdArg = args.remove(0).toLowerCase();
        GlacierCommand cmdObj = new UnknownCommand();

        if (COMMANDS.containsKey(cmdArg)) {
            cmdObj = COMMANDS.get(cmdArg);
        }

        try {
            cmdObj.setOut(System.out);
            cmdObj.setArgs(args);
            cmdObj.setClient(client);
            cmdObj.setCredentials(credentials);

            cmdObj.run();
        }
        catch (IllegalArgumentException e) {
            System.err.println(usage(cmdArg));
            System.err.println(e);
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("Command '" + cmdArg + "' failed:");
            System.err.println(e);
            System.exit(1);
        }
    }

    public static String usage() {
        String usage =  usage("<command>");
        usage += "\n\n";
        usage += "Command can be one of the following:";

        for (String cmd : COMMANDS.keySet()) {
            usage += "\n    " + cmd;
        }

        return usage;
    }

    public static String usage(String cmd) {
        return "App " + cmd;
    }
}
