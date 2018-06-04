package com.szm.pdf.demo.util;

import cn.hutool.core.util.ReUtil;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReUtilTests {

    @Test
    public void testReadPdf() throws Exception, IOException {
        PDDocument helloDocument = null;
//        helloDocument = PDDocument.load(new File("E:\\13_48_52_1527745732093_129876.pdf"));
        helloDocument = PDDocument.load(new File("E:\\word2pdf_out.pdf"));
        PDFTextStripper textStripper = new PDFTextStripper();
        System.out.println(textStripper.getText(helloDocument));

        helloDocument.close();
    }
    @Test
    public void testOutputPdfbyTemplet() throws Exception, IOException {
        PDDocument pdf = PDDocument.load(new File("E:\\word2pdf.pdf"));
        InputStream templatePdf = new FileInputStream("E:\\word2pdf.pdf");
//        PDDocumentCatalog docCatalog = pdf.getDocumentCatalog();
//        PDAcroForm acroForm = docCatalog.getAcroForm();
//        PDFont font = PDType1Font.HELVETICA_BOLD;//// TODO: 2018/6/1
//
//        PDField realNameField = acroForm.getField("fill_1");
//        PDField ageField = acroForm.getField("Text3");
//        PDField dateField = acroForm.getField("Text2");
//
//        realNameField.setValue( "1111");
//        ageField.setValue( "28");
//        dateField.setValue( "2017-12-11");
//        pdf.save(new File("E:\\word2pdf_out.pdf") );
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("fill_1","1111");
        paraMap.put("Text3","28");
        paraMap.put("Text2","2017-12-11");
        Pdf(templatePdf,"E:\\\\word2pdf_out.pdf",paraMap);
    }


    public static String Pdf(InputStream templatePdf, String filePath, Map<String, String> paraMap) throws Exception {
        PdfReader reader =null;
        try {
            File pdfFile = new File(filePath);
            if(pdfFile.exists()) {
                File file =new File(filePath);
                file.delete();
            }
            reader = new PdfReader(templatePdf);
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(filePath));
            BaseFont bf = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            @SuppressWarnings("unused")
            Font FontChinese = new Font(bf, 12, Font.NORMAL);
            AcroFields forms = stamp.getAcroFields();
            forms.addSubstitutionFont(bf);
            for (String key : paraMap.keySet()) {
                if("null".equals(paraMap.get(key))){
                    forms.setField(key, "");
                }else{
                    forms.setField(key, paraMap.get(key));
                }
            }
            stamp.setFormFlattening(true);
            stamp.close();
        } catch (Exception de) {
            System.err.println(de.getMessage());
            filePath = null;
        }finally {
            reader.close();
        }
        return filePath;
    }


}
