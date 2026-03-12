package com.conversion.microservice_conversion.kafka;

import com.conversion.microservice_conversion.dto.ConvertedFileEvent;
import com.conversion.microservice_conversion.dto.FileEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileEventProducer {

    private static final String INPUT_TOPIC = "file-upload-topic";
    private static final String OUTPUT_TOPIC = "file-converted-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(FileEvent event) {
        sendToTopic(INPUT_TOPIC, event);
    }

    public void sendConverted(ConvertedFileEvent event) {
        sendToTopic(OUTPUT_TOPIC, event);
    }

    private void sendToTopic(String topic, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, json);
            log.info("Sent to topic '{}': {}", topic, json);
        } catch (Exception e) {
            log.error("Failed to send message to topic '{}': {}", topic, e.getMessage(), e);
        }
    }
}