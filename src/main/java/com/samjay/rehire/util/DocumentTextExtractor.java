package com.samjay.rehire.util;

import com.samjay.rehire.exception.ApplicationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class DocumentTextExtractor {

    public String extractTextFromDocument(MultipartFile file) {

        try {

            String filename = file.getOriginalFilename();

            assert filename != null;

            if (filename.endsWith(".txt")) {

                return new String(file.getBytes());

            } else if (filename.endsWith(".docx")) {

                return readDocx(file.getInputStream());

            } else if (filename.endsWith(".pdf")) {

                return readPdf(file.getInputStream());

            } else {

                throw new ApplicationException("Unsupported file type: " + filename, HttpStatus.BAD_REQUEST);

            }
        } catch (Exception ex) {

            throw new ApplicationException("An unexpected error occurred", HttpStatus.BAD_REQUEST);

        }
    }

    private static String readDocx(InputStream inputStream) {

        try (XWPFDocument document = new XWPFDocument(inputStream)) {

            StringBuilder sb = new StringBuilder();

            for (XWPFParagraph para : document.getParagraphs()) {

                sb.append(para.getText()).append("\n");

            }

            return sb.toString();

        } catch (Exception ex) {

            return ex.getMessage();

        }
    }

    private static String readPdf(InputStream inputStream) {

        try (PDDocument document = PDDocument.load(inputStream)) {

            return new PDFTextStripper().getText(document);

        } catch (Exception ex) {

            return ex.getMessage();

        }
    }
}