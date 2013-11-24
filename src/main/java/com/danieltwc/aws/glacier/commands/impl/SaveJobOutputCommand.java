package com.danieltwc.aws.glacier.commands.impl;

import java.io.IOException;

import com.danieltwc.aws.glacier.commands.VaultCommand;
import com.danieltwc.aws.glacier.helpers.SaveJobOutputHelper;

public class SaveJobOutputCommand extends VaultCommand {
    public void run() throws InterruptedException, IOException, Exception {
        super.run();

        if (args.size() < 2) {
            throw new IllegalArgumentException("Must specify a job ID and an output filename");
        }

        String jobId = args.get(0);
        String filename = args.get(1);

        out.println("Downloading output for job '" + jobId + "'...");

        SaveJobOutputHelper helper = new SaveJobOutputHelper();
        helper.run(client, vaultName, jobId, filename);

        out.println("Job output for '" + vaultName + "' saved to '" + filename + "'");
    }
}
