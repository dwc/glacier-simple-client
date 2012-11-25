package com.danieltwc.aws.glacier.commands.impl;

import java.io.File;
import java.io.FileNotFoundException;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;

import com.danieltwc.aws.glacier.commands.GlacierCommand;

public class UploadCommand extends GlacierCommand {
    public void run() throws FileNotFoundException {
        if (args.size() < 2) {
            throw new IllegalArgumentException("upload <file> <comment>");
        }

        String archiveFilename = args.get(0);
        String comment = args.get(1);

        out.println("Uploading [" + archiveFilename + "] to vault [" + vaultName + "]...");

        UploadResult result = upload(vaultName, archiveFilename, comment);
        String archiveId = result.getArchiveId();

        out.println("Uploaded [" + archiveFilename + "] to vault [" + vaultName + "] and received archive ID [" + archiveId + "]");
    }

    private UploadResult upload(String vaultName, String archiveFilename, String comment) throws FileNotFoundException {
        ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);

        File file = new File(archiveFilename);
        return atm.upload(vaultName, comment, file);
    }
}
