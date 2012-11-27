package com.danieltwc.aws.glacier.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;

public class SaveJobOutputHelper {
    public void run(AmazonGlacierClient client, String vaultName, String jobId, String filename) throws InterruptedException, IOException, Exception {
        GetJobOutputResult jobOutputResult = downloadJobOutput(client, vaultName, jobId);
        saveJobOutput(jobOutputResult, filename);
    }

    private GetJobOutputResult downloadJobOutput(AmazonGlacierClient client, String vaultName, String jobId) {
        GetJobOutputRequest request = new GetJobOutputRequest()
            .withVaultName(vaultName)
            .withJobId(jobId);
        GetJobOutputResult result = client.getJobOutput(request);

        return result;
    }

    private void saveJobOutput(GetJobOutputResult result, String filename) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(filename));
        BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody()));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
        }
        finally {
            try { reader.close(); } catch (Exception e) {}
            try { writer.close(); } catch (Exception e) {}
        }
    }
}
