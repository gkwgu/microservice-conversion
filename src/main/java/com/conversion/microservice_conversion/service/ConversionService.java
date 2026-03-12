package com.conversion.microservice_conversion.service;

import com.conversion.microservice_conversion.converter.ConverterFactory;
import com.conversion.microservice_conversion.converter.FileConverter;
import com.conversion.microservice_conversion.dto.ConvertedFileEvent;
import com.conversion.microservice_conversion.dto.FileEvent;
import com.conversion.microservice_conversion.kafka.FileEventProducer;
import com.conversion.microservice_conversion.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionService {

    private final MinioService minioService;
    private final ConverterFactory converterFactory;
    private final FileEventProducer producer;

    public void process(FileEvent event) throws Exception {
        log.info("Processing file: bucket={}, path={}", event.getBucket(), event.getPath());

        InputStream fileStream = minioService.download(event.getBucket(), event.getPath());

        String ext = FileUtils.getExtension(event.getPath());
        FileConverter converter = converterFactory.getConverter(ext);

        File pdf = converter.convert(fileStream);

        String outputPath = event.getPath().replaceAll("\\.[^.]+$", "") + ".pdf";

        minioService.upload(event.getBucket(), outputPath, pdf);
        pdf.delete();

        ConvertedFileEvent convertedEvent = new ConvertedFileEvent(
                event.getBucket(),
                outputPath,
                event.getPath()
        );
        producer.sendConverted(convertedEvent);

        log.info("Conversion done: {} -> {}", event.getPath(), outputPath);
    }
}

