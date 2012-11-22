package com.danieltwc.aws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;

public class HomeDirectoryAWSCredentialsProvider implements AWSCredentialsProvider {
    private static final String DEFAULT_PROPERTIES_FILE = ".AwsCredentials.properties";

    private final File credentialsFile;

    public HomeDirectoryAWSCredentialsProvider() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public HomeDirectoryAWSCredentialsProvider(String credentialsFilePath) {
        if (credentialsFilePath == null) {
            throw new IllegalArgumentException("Credentials file path cannot be null");
        }

        File credentialsFile = new File(
            System.getProperty("user.home"),
            credentialsFilePath
        );

        this.credentialsFile = credentialsFile;
    }

    public AWSCredentials getCredentials() {
        try {
            InputStream properties = new FileInputStream(credentialsFile);
            return new PropertiesCredentials(properties);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load AWS credentials from the " + credentialsFile.toString() + " file", e);
        }
    }

    public void refresh() {}
}
