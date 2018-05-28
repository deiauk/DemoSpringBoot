package com.example.lalala.demo.services;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Path rootLocation = Paths.get("images");

    public String store(MultipartFile file) {
        String extension = file.getOriginalFilename().split("\\.")[1];
        String name = System.currentTimeMillis() + "." + extension;
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(name));
        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
        return name;
    }

    private Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("FAIL! " + resource.exists() + " " + resource.isReadable());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL! " + e.getMessage());
        }
    }

    public byte[] loadFileAsByteArray(String filename) {
        Resource resource = loadFile(filename);
        try {
            return IOUtils.toByteArray(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage! " + e.getMessage() + " " + rootLocation.toString());
        }
    }
}
