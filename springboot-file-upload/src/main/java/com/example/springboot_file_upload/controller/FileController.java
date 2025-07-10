package com.example.springboot_file_upload.controller;

import com.example.springboot_file_upload.model.FileInfo;
import com.example.springboot_file_upload.service.FileStorageService;
//import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;



import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file using the service
            fileStorageService.save(file);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/uploadMultiple")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> messages = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileStorageService.save(file);
                messages.add("Uploaded successfully: " + file.getOriginalFilename());
            } catch (Exception e) {
                messages.add("Failed to upload: " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() throws IOException {
        List<FileInfo> files = fileStorageService.loadAll()
                .map(path -> new FileInfo(
                        path.getFileName().toString(),
                        "http://localhost:8081/files/" + path.getFileName().toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path file = fileStorageService.load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
