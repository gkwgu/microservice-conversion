package com.conversion.microservice_conversion.converter;

import java.io.File;
import java.io.InputStream;

public interface FileConverter {

    boolean supports(String extension);

    File convert(InputStream inputStream) throws Exception;

}
