package com.conversion.microservice_conversion.converter;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class TxtToPdfConverter implements FileConverter {

    @Override
    public boolean supports(String extension) {
        return extension.equalsIgnoreCase("txt");
    }

    @Override
    public File convert(InputStream inputStream) throws Exception {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(50, 750);

        float y = 750;
        String line;

        while ((line = reader.readLine()) != null) {
            if (y < 50) {
                content.endText();
                content.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 750);
                y = 750;
            }

            String safeLine = line.chars()
                    .filter(c -> c < 256 && PDType1Font.HELVETICA.getEncoding()
                            .contains(new String(Character.toChars(c))))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            content.showText(safeLine);
            content.newLineAtOffset(0, -15);
            y -= 15;
        }

        content.endText();
        content.close();

        File file = File.createTempFile("converted", ".pdf");
        document.save(file);
        document.close();
        return file;
    }
}