package com.kritacademy.storage;

import com.kritacademy.exceptions.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by chertpong.github.io on 22/06/2016.
 * reference: https://github.com/spring-guides/gs-uploading-files/blob/master/complete/src/main/java/hello/storage/FileSystemStorageService.java
 */
@Service
public class StorageServiceImpl implements StorageService{
    private final Path rootLocation;
    @Autowired
    public StorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename() + " file is empty");
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public String store(MultipartFile file, Boolean usingHash, Boolean onlyImage) {
        if(onlyImage) {
            if(!isImage(file.getContentType())){
                throw new RuntimeException("Invalid image extension, accept only PNG/JPG/JPEG");
            }
        }
        if(usingHash) {
            try {
                if (file.isEmpty()) {
                    throw new StorageException("Failed to store empty file " + file.getOriginalFilename() + " file is empty");
                }
                String[] originalFilename = file.getOriginalFilename().split("\\.");
                String fileExtension = originalFilename[originalFilename.length - 1 ];
                String newFilename = UUID.randomUUID().toString() + "." + fileExtension;
                Files.copy(file.getInputStream(), this.rootLocation.resolve(newFilename));
                return newFilename;
            } catch (IOException e) {
                throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
            }
        }
        else {
            this.store(file);
            return file.getOriginalFilename();
        }
    }

    private Boolean isImage(String type) {
        return type.equals(MimeTypeUtils.IMAGE_JPEG_VALUE) || type.equals(MimeTypeUtils.IMAGE_PNG_VALUE);
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new DataNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new DataNotFoundException("Could not read file: " + filename);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public List<Path> move(String targetDirectory, List<String> filenames) { //TODO: test this
        Path target = createDirectoryUnderRootLocation(targetDirectory);
        return filenames.stream()
                .map(this::load)
                .map(file -> {
                    try {
                        return Files.move(file,target.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Path createDirectoryUnderRootLocation(String target) {
        Path targetDirectory = rootLocation.resolve(target);
        if(!Files.exists(targetDirectory)){
            try {
                Files.createDirectory(targetDirectory);
            }
            catch (IOException e) {
                throw new StorageException("Could not create folder", e);
            }
        }
        return targetDirectory;
    }
}
