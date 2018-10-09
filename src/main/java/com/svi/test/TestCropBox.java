package com.svi.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;

public class TestCropBox {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String input = "C:/Users/nbriones/Documents/TASKS/October 2018/10-1-2018 Cropping Tool (Sir James)/cropped images/100 bad images PDF/Scan_0012_cropped.pdf";
		String output = "C:/Users/nbriones/Documents/TASKS/October 2018/10-1-2018 Cropping Tool (Sir James)/cropped images/100 bad images PDF/Scan_0012_cropped_check.pdf";

		PDDocument doc = PDDocument.load(new File(input));
		PDPage page = doc.getPage(0);
		page.setCropBox(new PDRectangle(258, 423, 200, 400));
		doc.save(output);
		doc.close();
		
	}
	/**
	 * 			method to crop without change page size
	 * 			kept for reference purposes
	 * @param SRC
	 * @param DEST
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void cropPDF(String SRC, String DEST) throws FileNotFoundException, IOException{
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        int n = pdfDoc.getNumberOfPages();
        PdfDictionary page;
        PdfArray media;
        for (int p = 1; p <= n; p++) {
            page = pdfDoc.getPage(p).getPdfObject();
            media = page.getAsArray(PdfName.CropBox);
            if (media == null) {
                media = page.getAsArray(PdfName.MediaBox);
            }
            float x = 0;
            float y = 0;
            float width = media.getAsNumber(2).floatValue() + 10;
            float height = media.getAsNumber(3).floatValue() + 10;
            
            Rectangle pageSize = pdfDoc.getPage(p).getPageSize();
            float llx = (float) x;
            float lly = (float) y;
            float w = (float) width;
            float h = (float) height;
            
            System.out.println("lowerleft origin is : " + llx + "," + lly);
            System.out.println("PDF width is : " + w);
            System.out.println("PDF height is : " + h);
            
            if(llx < 0) llx = 0f;
            if(lly < 0) lly = 0f;
            
            // !IMPORTANT to write Locale
            String command = String.format(
                    Locale.ENGLISH,
                    "\nq %.2f %.2f %.2f %.2f re W n\nq\n",
                    llx, lly, w, h);
            
            new PdfCanvas(pdfDoc.getPage(p).newContentStreamBefore(), new PdfResources(), pdfDoc).writeLiteral(command);
            new PdfCanvas(pdfDoc.getPage(p).newContentStreamAfter(), new PdfResources(), pdfDoc).writeLiteral("\nQ\nQ\n");
        }
        pdfDoc.close();
	}

}
