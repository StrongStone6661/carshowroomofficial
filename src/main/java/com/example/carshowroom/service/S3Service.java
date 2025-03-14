package com.example.carshowroom.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;

    /*
     * Setting up the AWS S3 connection
     */
    @Value("${AWS_BUCKETNAME}")
    private String bucketName;

    @Value("${AWS_REGION}")
    private String region; // Added this so we can reuse it

    public S3Service(@Value("${AWS_REGION}") String region,
                     @Value("${secrets.AWS_ACCESS_KEY_ID}") String accessKey,
                     @Value("${secrets.AWS_SECRET_ACCESS_KEY}") String secretKey) {

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /*
     * Uploading a file to the S3 bucket
     */
    public String uploadFile(String fileName, byte[] fileData) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromBytes(fileData));

            return "File uploaded successfully: " + fileName;
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    /*
     * Listing files from the S3 bucket and returning their public URLs
     */
    public List<String> listFiles() {
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build());

            // Constructing the S3 URL for each object
            return response.contents().stream()
                    .map(s3Object -> "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Object.key())
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list files: " + e.getMessage());
        }
    }
}
