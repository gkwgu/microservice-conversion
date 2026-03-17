package com.conversion.microservice_conversion.kafka;

import com.conversion.microservice_conversion.dto.ConvertedFileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileEventProducer {

    @Value("${kafka.topics.output}")
    private String outputTopic;

    private final KafkaTemplate<String, ConvertedFileEvent> kafkaTemplate;

    public void sendConverted(ConvertedFileEvent event) {
        kafkaTemplate.send(outputTopic, event);
        log.info("Sent to topic '{}': {}", outputTopic, event);
    }
}