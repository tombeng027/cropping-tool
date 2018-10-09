package com.svi.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class TestJpegCompression {

    public static void main(String[] args) throws IOException {

        File imageFile = new File("C:/Users/nbriones/Documents/TASKS/October 2018/10-1-2018 Cropping Tool (Sir James)/test/Scan_0013.jpg");
        
        InputStream is = new FileInputStream(imageFile);
       
        
        // create a BufferedImage as the result of decoding the supplied InputStream
        BufferedImage image = ImageIO.read(is);


        // get all image writers for JPG format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext())
           throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        
        ImageWriteParam param = writer.getDefaultWriteParam();
        // compress to a given quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        OutputStream os = null;
        ImageOutputStream ios = null;
        List<Float> qualityList = new ArrayList<>();
        qualityList.add(0f);
        qualityList.add(0.1f);
        qualityList.add(0.2f);
        qualityList.add(0.3f);
        qualityList.add(0.4f);
        qualityList.add(0.5f);
        qualityList.add(0.6f);
        qualityList.add(0.7f);
        qualityList.add(0.8f);
        qualityList.add(0.9f);
        qualityList.add(1f);
        for(Float quality : qualityList){
	        String fileName = "C:/Users/nbriones/Documents/TASKS/October 2018/10-1-2018 Cropping Tool (Sir James)/test/research/myimage_compressed" + quality + ".jpg";
	        File compressedImageFile = new File(fileName);
	        os = new FileOutputStream(compressedImageFile);
	        ios = ImageIO.createImageOutputStream(os);
	        writer.setOutput(ios);
	        System.out.println(quality);
	        param.setCompressionQuality(quality);
	        // appends a complete image stream containing a single image and
	        //associated stream and image metadata and thumbnails to the output
	        writer.write(null, new IIOImage(image, null, null), param);
	        // close all streams
        }
        
        is.close();
        os.close();
        ios.close();
        writer.dispose();
    }
}

