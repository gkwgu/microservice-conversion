package com.conversion.microservice_conversion.service;

import com.conversion.microservice_conversion.dto.FileEvent;
import com.conversion.microservice_conversion.kafka.FileEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;
    private final FileEventProducer fileEventProducer;

    public void processFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        minioService.upload("files", fileName, file);
        fileEventProducer.send(new FileEvent("files", fileName));
    }
}