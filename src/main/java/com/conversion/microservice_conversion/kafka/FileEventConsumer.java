package com.conversion.microservice_conversion.kafka;

import com.conversion.microservice_conversion.dto.FileEvent;
import com.conversion.microservice_conversion.service.ConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileEventConsumer {

    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "file-upload-topic", groupId = "file-converter-group")
    public void consume(String message) {
        try {
            FileEvent event = objectMapper.readValue(message, FileEvent.class);
            conversionService.process(event);
        } catch (Exception e) {
            log.error("Failed to process message: {}, error: {}", message, e.getMessage());
        }
    }
}