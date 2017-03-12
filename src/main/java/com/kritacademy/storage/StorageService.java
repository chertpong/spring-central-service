package com.kritacademy.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by chertpong.github.io on 22/06/2016.
 * refernce: https://github.com/spring-guides/gs-uploading-files/blob/master/complete/src/main/java/hello/storage/StorageService.java
 */
public interface StorageService {

    void init();

    void store(MultipartFile file);

    String store(MultipartFile file, Boolean usingHash, Boolean onlyImage);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    List<Path> move(String targetDirectory, List<String> filenames);

    Path createDirectoryUnderRootLocation(String target);
}