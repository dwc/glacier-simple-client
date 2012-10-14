package com.danieltwc.aws.glacier;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;

public class App {
    static AWSCredentials credentials;
    static AmazonGlacierClient client;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("App <properties> <vault> <command> [...]");
            System.exit(1);
        }

        String propertiesFilename = args[0];
        String vaultName = args[1];
        String command = args[2];

        try {
            InputStream properties = new FileInputStream(propertiesFilename);
            credentials = new PropertiesCredentials(properties);
        }
        catch (IOException e) {
            System.err.println("Could not find AWS credentials:");
            System.err.println(e);
            System.exit(1);
        }

        try {
            client = new AmazonGlacierClient(credentials);

            if (command.equalsIgnoreCase("upload")) {
                if (args.length < 5) {
                    System.err.println("App <properties> <vault> upload <file> <comment>");
                    System.exit(1);
                }

                String archiveFilename = args[3];
                String comment = args[4];

                System.out.println("Uploading [" + archiveFilename + "] to vault [" + vaultName + "]...");
                UploadResult result = upload(vaultName, archiveFilename, comment);
                String archiveId = result.getArchiveId();
                System.out.println("Uploaded [" + archiveFilename + "] to vault [" + vaultName + "] and received archive ID [" + archiveId + "]");
            }
            else if (command.equalsIgnoreCase("delete")) {
                if (args.length < 4) {
                    System.err.println("App <properties> <vault> delete <archive>");
                    System.exit(1);
                }

                String archiveId = args[3];

                System.out.println("Deleting [" + archiveId + "] from vault [" + vaultName + "]...");
                client.deleteArchive(new DeleteArchiveRequest()
                                     .withVaultName(vaultName)
                                     .withArchiveId(archiveId));
                System.out.println("Deleted [" + archiveId + "] from vault [" + vaultName + "]");
            }
            else {
                System.err.println("Unknown command [" + command + "]");
            }
        }
        catch (Exception e) {
            System.err.println("Command [" + command + "] failed:");
            System.err.println(e);
            System.exit(1);
        }
    }

    public static UploadResult upload(String vaultName, String archiveFilename, String comment) throws FileNotFoundException {
        ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);

        File file = new File(archiveFilename);
        return atm.upload(vaultName, comment, file);
    }
}
