package com.danieltwc.aws.glacier.commands.impl;

import com.amazonaws.services.glacier.model.DeleteArchiveRequest;

import com.danieltwc.aws.glacier.commands.GlacierCommand;

public class DeleteCommand extends GlacierCommand {
    public void run() {
        if (args.length < 1) {
            throw new IllegalArgumentException("delete <archive>");
        }

        String archiveId = args[0];

        out.println("Deleting [" + archiveId + "] from vault [" + vaultName + "]...");
        client.deleteArchive(new DeleteArchiveRequest()
                             .withVaultName(vaultName)
                             .withArchiveId(archiveId));
        out.println("Deleted [" + archiveId + "] from vault [" + vaultName + "]");
    }
}
