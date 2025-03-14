package com.example.carshowroom.controllers;

import com.example.carshowroom.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String message = s3Service.uploadFile(file.getOriginalFilename(), file.getBytes());
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> getImages(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        List<String> files = s3Service.listFiles();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, files.size());

        if (fromIndex >= files.size()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(files.subList(fromIndex, toIndex));
    }
}
