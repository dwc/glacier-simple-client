package com.danieltwc.aws.glacier.commands.impl;

import java.io.File;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

import com.danieltwc.aws.glacier.commands.VaultCommand;

public class DownloadCommand extends VaultCommand {
    public void run() throws Exception {
        super.run();

        if (args.size() < 2) {
            throw new IllegalArgumentException("Must specify an archive ID and output filename");
        }

        String archiveId = args.get(0);
        String filename = args.get(1);

        out.println("Downloading '" + archiveId + "' from vault '" + vaultName + "' to file '" + filename + "'...");

        download(vaultName, archiveId, filename);

        out.println("Downloaded '" + archiveId + "' from vault '" + vaultName + "' to file '" + filename + "'");
    }

    private void download(String vaultName, String archiveId, String filename) {
        ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);

        File file = new File(filename);
        atm.download(vaultName, archiveId, file);
    }
}
