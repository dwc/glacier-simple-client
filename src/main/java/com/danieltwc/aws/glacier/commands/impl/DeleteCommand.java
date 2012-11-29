package com.danieltwc.aws.glacier.commands.impl;

import com.amazonaws.services.glacier.model.DeleteArchiveRequest;

import com.danieltwc.aws.glacier.commands.GlacierCommand;

public class DeleteCommand extends GlacierCommand {
    public void run() {
        if (args.size() < 1) {
            throw new IllegalArgumentException("<archive>");
        }

        String archiveId = args.get(0);

        out.println("Deleting [" + archiveId + "] from vault [" + vaultName + "]...");

        DeleteArchiveRequest request = new DeleteArchiveRequest()
            .withVaultName(vaultName)
            .withArchiveId(archiveId);
        client.deleteArchive(request);

        out.println("Deleted [" + archiveId + "] from vault [" + vaultName + "]");
    }
}
