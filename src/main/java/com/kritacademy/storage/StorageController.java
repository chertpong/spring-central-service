package com.kritacademy.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@RestController
public class StorageController {
    private final StorageService storageService;
    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/v1/api/uploads")
    public List<String> listUploadedFiles() throws IOException {

        return storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(StorageController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/v1/api/uploads")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        String filename = storageService.store(file, true, true);
        return MvcUriComponentsBuilder
                .fromMethodName(
                        StorageController.class,
                        "serveFile",
                        storageService.load(filename).getFileName().toString()
                )
                .build().toString();
    }

}
