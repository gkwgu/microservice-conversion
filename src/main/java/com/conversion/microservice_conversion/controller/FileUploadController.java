package com.conversion.microservice_conversion.controller;

import com.conversion.microservice_conversion.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        fileService.processFile(file);
        return "File sent to Kafka: " + file.getOriginalFilename();
    }
}