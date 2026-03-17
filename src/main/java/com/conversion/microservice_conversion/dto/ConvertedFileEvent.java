package com.conversion.microservice_conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertedFileEvent {

    private String bucket;
    private String path;
    private String sourcePath;

}
