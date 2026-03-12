package com.conversion.microservice_conversion.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


@Component
public class ImageToPdfConverter implements FileConverter {

    @Override
    public boolean supports(String extension) {
        return extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("jpeg");
    }

    @Override
    public File convert(InputStream inputStream) throws Exception {
        byte[] imageBytes = inputStream.readAllBytes();

        String realExtension = isPng(imageBytes) ? "png" : "jpg";

        File tempImage = File.createTempFile("image_src", "." + realExtension);
        try (FileOutputStream fos = new FileOutputStream(tempImage)) {
            fos.write(imageBytes);
        }

        try (PDDocument document = new PDDocument()) {
            PDImageXObject pdImage = PDImageXObject.createFromFile(
                    tempImage.getAbsolutePath(), document
            );

            PDRectangle pageSize = new PDRectangle(pdImage.getWidth(), pdImage.getHeight());
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            }

            File outputPdf = File.createTempFile("converted_image", ".pdf");
            document.save(outputPdf);
            return outputPdf;
        } finally {
            tempImage.delete();
        }
    }

    private boolean isPng(byte[] bytes) {
        return bytes.length >= 4
                && bytes[0] == (byte) 0x89
                && bytes[1] == (byte) 0x50
                && bytes[2] == (byte) 0x4E
                && bytes[3] == (byte) 0x47;
    }
}