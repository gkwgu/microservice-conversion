package com.conversion.microservice_conversion.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConverterFactory {

    private final List<FileConverter> converters;

    public FileConverter getConverter(String extension) {

        return converters.stream()
                .filter(c -> c.supports(extension))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Unsupported file type"));

    }

}
