package com.example.springboot_file_upload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileStorageService {
    private final Path path;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.path = Path.of(uploadDir);
        try{
           Files.createDirectories(this.path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public void save(MultipartFile file) throws IOException {
        Files.copy(file.getInputStream(), this.path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
    }

    public Stream<Path> loadAll() throws IOException {
        return Files.walk(this.path, 1)
                .filter(path -> !path.equals(this.path))
                .map(this.path::relativize);
    }

    public Path load(String filename) {
        return this.path.resolve(filename);
    }

}
