package com.example.carshowroom.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "carsroombucket"; 
    private static final String AWS_REGION = "us-east-1"; 

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String uploadFile(String fileName, byte[] fileData) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(fileName)
                            .build(),
                    RequestBody.fromBytes(fileData));

            return "File uploaded successfully: " + fileName;
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public List<String> listFiles() {
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .build());

            return response.contents().stream()
                    .map(s3Object -> "https://" + BUCKET_NAME + ".s3." + AWS_REGION + ".amazonaws.com/" + s3Object.key())
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list files: " + e.getMessage());
        }
    }
}
