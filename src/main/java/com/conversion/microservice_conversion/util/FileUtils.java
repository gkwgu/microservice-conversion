package com.conversion.microservice_conversion.util;

public class FileUtils {

    public static String getExtension(String name) {

        return name.substring(name.lastIndexOf(".") + 1);

    }

    public static String toPdfName(String name) {

        return name.replaceAll("\\.[^.]+$", ".pdf");

    }

}
