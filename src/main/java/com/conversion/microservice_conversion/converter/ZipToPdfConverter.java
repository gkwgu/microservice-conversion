package com.conversion.microservice_conversion.converter;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipToPdfConverter implements FileConverter {

    private final List<FileConverter> converters;

    public ZipToPdfConverter(List<FileConverter> converters) {
        this.converters = converters.stream()
                .filter(c -> !(c instanceof ZipToPdfConverter))
                .toList();
    }

    @Override
    public boolean supports(String extension) {
        return extension.equalsIgnoreCase("zip");
    }

    @Override
    public File convert(InputStream inputStream) throws Exception {
        List<File> convertedPdfs = new ArrayList<>();

        try (ZipInputStream zipStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (shouldSkip(entry)) {
                    zipStream.closeEntry();
                    continue;
                }

                String extension = getExtension(entry.getName());

                FileConverter converter = converters.stream()
                        .filter(c -> c.supports(extension))
                        .findFirst()
                        .orElse(null);

                if (converter == null) {
                    zipStream.closeEntry();
                    continue;
                }

                byte[] bytes = zipStream.readAllBytes();
                convertedPdfs.add(converter.convert(new ByteArrayInputStream(bytes)));
                zipStream.closeEntry();
            }
        }

        if (convertedPdfs.isEmpty()) {
            throw new RuntimeException("ZIP archive contains no supported files");
        }

        if (convertedPdfs.size() == 1) {
            return convertedPdfs.get(0);
        }

        return mergePdfs(convertedPdfs);
    }

    private boolean shouldSkip(ZipEntry entry) {
        String name = entry.getName();
        return entry.isDirectory()
                || name.startsWith("__MACOSX/")
                || name.startsWith(".")
                || name.contains("/.")
                || name.endsWith(".DS_Store")
                || getExtension(name).isEmpty();
    }

    private File mergePdfs(List<File> pdfFiles) throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();
        File merged = File.createTempFile("converted_zip", ".pdf");
        merger.setDestinationFileName(merged.getAbsolutePath());
        for (File pdf : pdfFiles) {
            merger.addSource(pdf);
        }
        merger.mergeDocuments(null);
        pdfFiles.forEach(File::delete);
        return merged;
    }

    private String getExtension(String fileName) {
        String simpleName = new File(fileName).getName();
        int dot = simpleName.lastIndexOf('.');
        return (dot < 0 || dot == simpleName.length() - 1) ? "" : simpleName.substring(dot + 1);
    }
}