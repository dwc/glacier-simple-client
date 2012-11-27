package com.danieltwc.aws.glacier.commands.impl;

import java.io.IOException;

import com.danieltwc.aws.glacier.commands.GlacierCommand;
import com.danieltwc.aws.glacier.helpers.SaveJobOutputHelper;

public class SaveJobOutputCommand extends GlacierCommand {
    public void run() throws InterruptedException, IOException, Exception {
        if (args.size() < 2) {
            throw new IllegalArgumentException("save-job-output <job-id> <filename>");
        }

        String jobId = args.get(0);
        String filename = args.get(1);

        out.println("Downloading output for job [" + jobId + "]...");

        SaveJobOutputHelper helper = new SaveJobOutputHelper();
        helper.run(client, vaultName, jobId, filename);

        out.println("Job output for [" + vaultName + "] saved to [" + filename + "]");
    }
}
