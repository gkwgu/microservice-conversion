package com.conversion.microservice_conversion.config;

import com.conversion.microservice_conversion.dto.ConvertedFileEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.input}")
    private String inputTopic;

    @Value("${kafka.topics.output}")
    private String outputTopic;

    private Serializer<ConvertedFileEvent> jsonSerializer(ObjectMapper objectMapper) {
        return new Serializer<>() {
            @Override
            public byte[] serialize(String topic, ConvertedFileEvent data) {
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize ConvertedFileEvent", e);
                }
            }
        };
    }

    @Bean
    public ProducerFactory<String, ConvertedFileEvent> producerFactory(ObjectMapper objectMapper) {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
                ),
                new StringSerializer(),
                jsonSerializer(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, ConvertedFileEvent> kafkaTemplate(
            ProducerFactory<String, ConvertedFileEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic inputTopic() {
        return TopicBuilder.name(inputTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name(outputTopic).partitions(1).replicas(1).build();
    }
}