package com.danieltwc.aws.glacier.commands.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import com.danieltwc.aws.glacier.commands.VaultCommand;
import com.danieltwc.aws.glacier.helpers.SaveJobOutputHelper;

public class InventoryCommand extends VaultCommand {
    private final String DEFAULT_FILENAME = "inventory.json";
    private final int SLEEP_TIME = 600;  // seconds

    private ObjectMapper jsonMapper;
    private JsonFactory jsonFactory;
    private AmazonSQSClient sqsClient;
    private AmazonSNSClient snsClient;

    public void run() throws InterruptedException, JsonParseException, IOException, Exception {
        super.run();

        if (args.size() < 2) {
            throw new IllegalArgumentException("Must specify an SNS topic ARN and an SQS queue URL");
        }

        String snsTopicARN = args.get(0);
        String sqsQueueURL = args.get(1);
        String filename = args.size() > 2 ? args.get(2) : DEFAULT_FILENAME;

        out.println("Retrieving inventory for '" + vaultName + "' using SNS topic '" + snsTopicARN + "' and SQS queue '" + sqsQueueURL + "'...");

        jsonMapper = new ObjectMapper();
        jsonFactory = jsonMapper.getJsonFactory();
        sqsClient = new AmazonSQSClient(credentials);
        snsClient = new AmazonSNSClient(credentials);

        out.println("Initiating job...");
        InitiateJobResult initiateJobResult = initiateJob(vaultName, snsTopicARN);
        String jobId = initiateJobResult.getJobId();

        out.println("Waiting for job '" + jobId + "' to complete...");
        Boolean success = waitForJobToComplete(jobId, sqsQueueURL);
        if (!success) {
            throw new Exception("Job '" + jobId + "' did not complete successfully!");
        }

        out.println("Saving inventory to '" + filename + "'...");

        SaveJobOutputHelper helper = new SaveJobOutputHelper();
        helper.run(client, vaultName, jobId, filename);

        out.println("Inventory for '" + vaultName + "' saved to '" + filename + "'");
    }

    private InitiateJobResult initiateJob(String vaultName, String snsTopicARN) {
        JobParameters params = new JobParameters()
            .withType("inventory-retrieval")
            .withSNSTopic(snsTopicARN);

        InitiateJobRequest jobRequest = new InitiateJobRequest()
            .withVaultName(vaultName)
            .withJobParameters(params);

        InitiateJobResult jobResult = client.initiateJob(jobRequest);

        return jobResult;
    }

    private Boolean waitForJobToComplete(String jobId, String sqsQueueURL) throws InterruptedException, JsonParseException, IOException {
        Boolean messageFound = false;
        Boolean jobSuccessful = false;

        while (!messageFound) {
            out.println("Checking for messages on queue '" + sqsQueueURL + "'...");

            ReceiveMessageRequest request = new ReceiveMessageRequest(sqsQueueURL)
                .withMaxNumberOfMessages(10);
            List<Message> msgs = sqsClient.receiveMessage(request).getMessages();
            int numMsgs = msgs.size();

            if (numMsgs > 0) {
                out.println("Found " + numMsgs + " message" + (numMsgs == 1 ? "" : "s") + "; looking for job '" + jobId + "'...");

                for (Message msg : msgs) {
                    JsonNode bodyNode = parseJSON(msg.getBody());
                    String body = bodyNode.get("Message").getTextValue();

                    JsonNode descriptionNode = parseJSON(body);
                    String retrievedJobId = descriptionNode.get("JobId").getTextValue();
                    String statusCode = descriptionNode.get("StatusCode").getTextValue();

                    if (retrievedJobId.equals(jobId)) {
                        out.println("Found job '" + retrievedJobId + "' and status '" + statusCode + "'!");

                        messageFound = true;

                        if (statusCode.equals("Succeeded")) {
                            jobSuccessful = true;
                        }
                    }
                }
            }
            else {
                out.println("No messages found; sleeping '" + SLEEP_TIME + "' seconds...");
                Thread.sleep(SLEEP_TIME * 1000);
            }
        }

        return (messageFound && jobSuccessful);
    }

    private JsonNode parseJSON(String json) throws IOException {
        JsonParser parser = jsonFactory.createJsonParser(json);
        JsonNode node = jsonMapper.readTree(parser);

        return node;
    }
}
