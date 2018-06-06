package com.szm.pdf.demo.util;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfTests {

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
    @Test
    public void testFindByKey() throws Exception, IOException {
        PDDocument pdf = PDDocument.load(new File("E:\\\\word2pdf_out.pdf"));
        List<float[]> list = getKeyWords("E:\\\\word2pdf_out.pdf","姓名");
        System.out.println(list);
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


    /**
     * 返回关键字所在页码和坐标
     * @param filePath  PDF位置
     * @param key     要定位的图片
     * @return  List<float[]>  返回关键字所在的坐标和页数 float[0] >> X; float[1] >> Y; float[2] >> page
     */
//    public List<float[]> getKeyWords(String filePath, final Image image) {
    public List<float[]> getKeyWords(String filePath, final String key) {
        final List<float[]> arrays = new ArrayList<float[]>();
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(filePath);
            int pageNum = pdfReader.getNumberOfPages();
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);

            for (int i = 1; i <= pageNum; i++) {

                final int finalI = i;
                pdfReaderContentParser.processContent(i, new RenderListener() {
                    //此方法是监听PDF里的文字内容，有重复情况会都把坐标和页码信息都存入arrays里
                    @Override
                    public void renderText(TextRenderInfo textRenderInfo) {
                            String text = textRenderInfo.getText(); // 整页内容

                            if (null != text && text.contains(key)) {
                                Rectangle2D.Float boundingRectange = textRenderInfo
                                        .getBaseline().getBoundingRectange();
                                float[] resu = new float[3];
                                resu[0] = (float)boundingRectange.getCenterX();
                                resu[1] = (float)boundingRectange.getCenterY();
                                resu[2] = finalI;
                                arrays.add(resu);
                            }
                    }

                    //此方法是监听PDF里的图片内容
                    @Override
                    public void renderImage(ImageRenderInfo arg0) {
//                        PdfImageObject image0;
//                        try {
//                            image0 = arg0.getImage();
//                            byte[] imageByte = image0.getImageAsBytes();
//                            Image imageInPDF = Image.getInstance(imageByte);
//                            if(image0!=null && imageInPDF.equals(image)){
//                                float[] resu = new float[3];
//                                // 0 => x;  1 => y;  2 => z
//                                //z的值始终为1
//                                resu[0] = arg0.getStartPoint().get(0);
//                                resu[1] = arg0.getStartPoint().get(1);
//                                resu[2] = finalI;
//                                arrays.add(resu);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (BadElementException e) {
//                            e.printStackTrace();
//                        }

                    }
                    @Override
                    public void endTextBlock() {
                    }
                    @Override
                    public void beginTextBlock() {

                    }
                });
            }
            pdfReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrays;
    }

    /**
     * 读取pdf内容
     */
    @Test
    public void readPdfToTxt() {
        String pdfPath="E:\\\\word2pdf_out.pdf";
        PdfReader reader = null;
        StringBuffer buff = new StringBuffer();
        try {
            reader = new PdfReader(pdfPath);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            int num = reader.getNumberOfPages();// 获得页数
            TextExtractionStrategy strategy;
            for (int i = 1; i <= num; i++) {
                strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                strategy = parser.processContent(i, new LocationTextExtractionStrategy());
                buff.append(strategy.getResultantText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(buff.toString());
    }
    /**
     * 读取pdf内容
     */
    @Test
    public void readPdfToTxt2() throws Exception{
        String pdfPath="E:\\\\word2pdf_out.pdf";
        float x1 = 220.00f;//起始坐标
        float y1 = 727.00f;
        float x2 = 309.72173f;//结束坐标
        float y2 = 727.8803f;
        PdfReader reader = new PdfReader(pdfPath); //抽取文件
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        Rectangle rect = new Rectangle(x1, y1, x2, y2);
        RenderFilter filter = new RegionTextRenderFilter(rect);
        RenderFilter[] a = {filter};
        FilteredTextRenderListener strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), a);
        strategy = parser.processContent(1, strategy);
        String text =  strategy.getResultantText();
        System.out.println(text);
    }


}
