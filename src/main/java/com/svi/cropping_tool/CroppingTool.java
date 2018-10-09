package com.svi.cropping_tool;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.svi.objects.Word;



public class CroppingTool {
	//Extremities/Outermost points in an image
	private static double leftMostPoint = 0;
	private static double rightMostPoint = 0;
	private static double highestPoint = 0;
	private static double lowestPoint = 0;

	private static int croppedWidth = 0;
	private static int croppedHeight = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		initializeConfig();
		File imageInputFolder = new File(AppConfig.IMAGE_INPUT_PATH.value());
		File coordinateInputFolder = new File(AppConfig.COORDINATE_INPUT_PATH.value());
		boolean useCustomOutput = (AppConfig.USE_CUSTOM_OUTPUT_PATH.value().equalsIgnoreCase("y")) ? true:false;
		String tiff = "_cropped.tif";
		String jpg = "_cropped.jpg";
		String pdf = "_cropped.pdf";
		String[] temp = imageInputFolder.getAbsolutePath().split("\\\\");
		String inputFolder = temp[temp.length -1];
		//for compiling the inputs into a list
		List<File> fileImages = new ArrayList<>();
		List<File> fileCoordinates = new ArrayList<>();
		listFiles(imageInputFolder, fileImages);
		listFiles(coordinateInputFolder, fileCoordinates);
		
		//loop to process each image in the list
		for(File image : fileImages){
			List<Word> wordsInImage = new ArrayList<>();
			boolean done = false;
			String fileNameWithoutExt = image.getName().substring(0, image.getName().length() - 4);
			String fileExtension = (image.getName().substring(image.getName().length() - 3));
			if(fileExtension.equals("jpg")){
				getCoordinateFileAndProcessImage(useCustomOutput, done, image, inputFolder, fileNameWithoutExt, jpg, fileExtension, fileCoordinates, wordsInImage);
			}else if(fileExtension.equals("tif")){
				getCoordinateFileAndProcessImage(useCustomOutput, done, image, inputFolder, fileNameWithoutExt, tiff, fileExtension, fileCoordinates, wordsInImage);
			}else if(fileExtension.equals("pdf")){
				getCoordinateFileAndProcessPDF(useCustomOutput, done, image, inputFolder, fileNameWithoutExt, pdf, fileExtension, fileCoordinates, wordsInImage);
			}
		}
		System.out.println("Done Cropping Images.");
		   
	}
	/**
	 * 					method to process PDF files
	 * @param useCustomOutput
	 * @param done
	 * @param image
	 * @param inputFolder
	 * @param fileNameWithoutExt
	 * @param ext
	 * @param fileExtension
	 * @param fileCoordinates
	 * @param wordsInImage
	 * @throws IOException
	 */
	public static void getCoordinateFileAndProcessPDF(boolean useCustomOutput, boolean done, File image, String inputFolder, String fileNameWithoutExt
			, String ext, String fileExtension, List<File> fileCoordinates, List<Word> wordsInImage) throws IOException{
		Map<String, List<Word>> wordMap = new HashMap<>();
		File croppedImageOutputFolder = new File(image.getParentFile().getAbsolutePath().replaceAll(inputFolder, "cropped images"));
		if(useCustomOutput)croppedImageOutputFolder = new File(AppConfig.OUTPUT_PATH.value());
		if(!croppedImageOutputFolder.exists())croppedImageOutputFolder.mkdirs();
		String outputName = fileNameWithoutExt + ext;;
		String newCoordinateName = image.getName().substring(0, image.getName().length() - 4) + " new_coordinates.txt";
		File newCoordinateFileOutput = new File(croppedImageOutputFolder + "/" + newCoordinateName);
		String imageName = image.getName().substring(0, image.getName().length() - 4);
		System.out.println("Processing " + image.getName());
			//loop for selecting the corresponding coordinate file in the list
			for(File coordinate : fileCoordinates){
				String coordinateName = coordinate.getName().substring(0, coordinate.getName().length() - 4);
				if(coordinateName.equalsIgnoreCase(imageName)){
					System.out.println("Using coordinate file " + coordinate.getName());							
					File ouputImageFile = new File(croppedImageOutputFolder + "/" + outputName);
					getCoordinates(coordinate,wordsInImage, fileExtension);
					cropPDFv2(image, ouputImageFile);
//					cropPDF(image.getAbsolutePath(), ouputImageFile.getAbsolutePath());
					System.out.println();
					System.out.println("done writing " + outputName);
					System.out.println();
					done = true;
					break;
				}
			}
			if(done == true){
//				adjustWordCoordinates(wordsInImage);
//				compileWordMap(wordsInImage, wordMap);
//				writeNewCoordinateFile(wordMap, newCoordinateFileOutput);
				resetExtremes();
			}else{
				System.out.println("no coordinate file found...");
			}
	}
	
	/**
	 * 			method to crop pdf file
	 * @param input
	 * @param output
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public static void cropPDFv2(File input, File output) throws InvalidPasswordException, IOException{
		PDDocument doc = PDDocument.load(new File(input.getAbsolutePath()));
		PDPage page = doc.getPage(0);
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(input));
		 Rectangle pageSize = pdfDoc.getPage(1).getPageSize();
		 float llx = (float) (leftMostPoint - pageSize.getWidth()*.01);
         float lly = (float) (lowestPoint - pageSize.getHeight()*.01);
         float w = (float) (rightMostPoint - leftMostPoint + ((pageSize.getWidth()*.01)*2));
         float h = (float) (highestPoint - lowestPoint + ((pageSize.getHeight()*.01)*2));
         if(llx < 0) llx = 0f;
         if(lly < 0) lly = 0f;
         
         croppedWidth = (int) llx;
         croppedHeight = (int) lly;
         
         System.out.printf("The origins are x: %f and y : %f",llx,lly);
         System.out.println("The new width is : " + w);
         System.out.println("The new height is : " + h);
         
		page.setCropBox(new PDRectangle(llx, lly, w, h));
		doc.save(output);
		doc.close();
		pdfDoc.close();
	}
	
	/*
	 * 	this part was commented out but it is another way to crop the image i haven't mastered it yet, and
	 * 	kept it here for reference purposes, 
	 */
//	public static void cropPDF(String SRC, String DEST) throws FileNotFoundException, IOException{
//		PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
//        int n = pdfDoc.getNumberOfPages();
//        PdfDictionary page;
//        PdfArray media;
//        for (int p = 1; p <= n; p++) {
//            page = pdfDoc.getPage(p).getPdfObject();
//            media = page.getAsArray(PdfName.CropBox);
//            if (media == null) {
//                media = page.getAsArray(PdfName.MediaBox);
//            }
//            
//            Rectangle pageSize = pdfDoc.getPage(p).getPageSize();
//            float llx = (float) (leftMostPoint - pageSize.getWidth()*.01);
//            float lly = (float) (lowestPoint - pageSize.getHeight()*.01);;
//            float w = (float) (rightMostPoint - leftMostPoint + ((pageSize.getWidth()*.01)*2));
//            float h = (float) (highestPoint - lowestPoint + ((pageSize.getHeight()*.01)*2));
//            
//            System.out.println("lowerleft origin is : " + llx + "," + lly);
//            System.out.println("PDF width is : " + w);
//            System.out.println("PDF height is : " + h);
//            
//            if(llx < 0) llx = 0f;
//            if(lly < 0) lly = 0f;
//            
//            // !IMPORTANT to write Locale
//            String command = String.format(
//                    Locale.ENGLISH,
//                    "\nq %.2f %.2f %.2f %.2f re W n\nq\n",
//                    llx, lly, w, h);
//            
//            Rectangle rectangle = new Rectangle(llx, lly, w, h);
//            
//            croppedHeight = Math.round(lly);
//            croppedWidth = Math.round(llx);
//            
//            
//            new PdfCanvas(pdfDoc.getPage(p).setMediaBox(rectangle).newContentStreamBefore(), new PdfResources(), pdfDoc).writeLiteral(command);
//            new PdfCanvas(pdfDoc.getPage(p).setMediaBox(rectangle).newContentStreamAfter(), new PdfResources(), pdfDoc).writeLiteral("\nQ\nQ\n");
//        }
//        pdfDoc.close();
//	}
	
	
	/**
	 * 				Method to get the corresponding coordinate file and process the image
	 * @param useCustomOutput	- defines what output folder path to use, false if default, true if custom
	 * @param done				- boolean to to check if the coordinate file is found and the process is done.
	 * @param image				- file input for the image
	 * @param inputFolder		- the path for the input folder
	 * @param fileNameWithoutExt- the filename without the file extension
	 * @param ext				- the string that will be added to the output after it is done processing
	 * @param fileExtension		- the input image file extension
	 * @param fileCoordinates	- the list of the coordinate files in the coordinates input folder
	 * @param wordsInImage		- the list of words found in the image being processed
	 * @throws IOException		
	 */
	public static void getCoordinateFileAndProcessImage(boolean useCustomOutput, boolean done, File image, String inputFolder, String fileNameWithoutExt
			, String ext, String fileExtension, List<File> fileCoordinates, List<Word> wordsInImage) throws IOException{
		Map<String, List<Word>> wordMap = new HashMap<>();
		File croppedImageOutputFolder = new File(image.getParentFile().getAbsolutePath().replaceAll(inputFolder, "cropped images"));
		if(useCustomOutput)croppedImageOutputFolder = new File(AppConfig.OUTPUT_PATH.value());
		if(!croppedImageOutputFolder.exists())croppedImageOutputFolder.mkdirs();
		String outputName = fileNameWithoutExt + ext;;
		String newCoordinateName = image.getName().substring(0, image.getName().length() - 4) + " new_coordinates_" + fileExtension + ".txt";
		File newCoordinateFileOutput = new File(croppedImageOutputFolder + "/" + newCoordinateName);
		String imageName = image.getName().substring(0, image.getName().length() - 4);
		System.out.println("Processing " + image.getName());
			//loop for selecting the corresponding coordinate file in the list
			for(File coordinate : fileCoordinates){
				String coordinateName = coordinate.getName().substring(0, coordinate.getName().length() - 4);
				if(coordinateName.equalsIgnoreCase(imageName + "_" + fileExtension)){
					System.out.println("Using coordinate file " + coordinate.getName());							
					File ouputImageFile = new File(croppedImageOutputFolder + "/" + outputName);
					getCoordinates(coordinate,wordsInImage, fileExtension);
					BufferedImage test = ImageIO.read(image);
					cropImage(test, ouputImageFile, fileExtension, image);
					System.out.println();
					System.out.println("done writing " + outputName);
					System.out.println();
					done = true;
					break;
				}
			}
			if(done == true){
				adjustWordCoordinates(wordsInImage);
				compileWordMap(wordsInImage, wordMap);
				writeNewCoordinateFile(wordMap, newCoordinateFileOutput);
				resetExtremes();
			}else{
				System.out.println("no coordinate file found...");
			}
	}
	
	/**
	 * 			Method to crop the image by using the stored coordinates to compute the (x,y) origin of the 
	 * 			cropped output and its new width and height.
	 * @param bufferedImage		- the input jpg image
	 * @param output			- cropped jpg image output
	 * @throws IOException
	 */
	public static void cropImage(BufferedImage bufferedImage, File output, String ext, File image) throws IOException{
		//setting up/computing the parameters for the cropped image
		double heightOrigin = bufferedImage.getHeight();
		double widthOrigin = bufferedImage.getWidth();
		double heightDiff = bufferedImage.getHeight() * 0.01;	//top & bottom margin
		double widthDiff = bufferedImage.getWidth() * 0.01;  //left and right margin
		int x = (int) (leftMostPoint - widthDiff) + 1; // leftmost origin of the cropped image(+1 to compensate the decimal)
		int y = (int) (highestPoint - heightDiff) + 1;// topmost origin of the cropped image(+1 to compensate the decimal)
		if(x < 0) x = 0;	//0 to prevent negative coordinates
		if(y < 0) y = 0;
		int width = (int) (rightMostPoint - x + widthDiff) + 1; //width of the cropped image
		int height = (int) (lowestPoint - y + heightDiff) + 1;	//height of the cropped image
		if(width > widthOrigin) width = (int) widthOrigin;	//for preventing error when the computed new width is greater the original width
		if(height > heightOrigin) height = (int) heightOrigin;
		if(y > 0 && (y + height) > heightOrigin){
			y = (int) Math.abs((y + height) - (y + heightOrigin)); //for preventing the error when (y + height) is greater the the original height
		}														 // (y + height) will be the bottom most coordinate of the cropped image. same as (x + width) will be the rightmost coordinate
		if(x > 0 && (x + width) > widthOrigin){
			x = (int) Math.abs((x + width) - (x + widthOrigin));	
		}
		croppedWidth = x;
		croppedHeight = y;
		System.out.println("The crop will start at x coordinate " + x + " y coordinate " + y);
		System.out.println("The width margin will be : " + widthDiff);
		System.out.println("The height margin will be : " + heightDiff);
		System.out.println("The cropped image width is " + (width));
		System.out.println("The cropped image height is " + (height));
		System.out.println("The original width is " + widthOrigin);
		System.out.println("The original height is " + heightOrigin);
		System.out.println("Writing cropped image to file...");
		System.out.println(output.getAbsolutePath());
		if(ext.equals("jpg")){
			cropJPGImage(bufferedImage, output, x, y, width, height, ext, image);		
		}else if(ext.equals("tif")){
			cropTIFFImage(bufferedImage, output, x, y, width, height, ext, image);
		}
	}
	
	/**
	 * 				method to crop the image if it is a jpeg file
	 * @param bufferedImage	- the input image
	 * @param output		- the output file where the cropped image will be written
	 * @param x				- the x origin of the crop based on the input coordinates
	 * @param y				- the y origin of the crop based on the input coordinates
	 * @param width			- the width of the crop box
	 * @param height		- the height of the crop box
	 * @param ext			- the file extension
	 * @throws IOException
	 */
	public static void cropJPGImage(BufferedImage bufferedImage, File output, int x, int y, int width, int height, String ext, File image) throws IOException{
		BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
	    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(ext);
        if (!writers.hasNext())
           throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.ENGLISH);
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        OutputStream os = new FileOutputStream(output);
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	    writer.setOutput(ios);
	    float quality = 0.5f;
	    param.setCompressionType("JPEG");
	    param.setCompressionQuality(quality);  
//	    ImageReader reader = ImageIO.getImageReader(writer);	// for trying to get the same metadata as the input
//	    reader.setInput(stream);
//	    IIOMetadata meta = reader.getImageMetadata(0);
	    writer.write(null, new IIOImage(croppedImage, null, null), param);
        ios.close();
        os.close();
        writer.dispose();
//      reader.dispose();
	}
	
	/**
	 * 				the method to be called to process a tiff image
	 * @param bufferedImage	-	the input image
	 * @param output		-	the output file
	 * @param x				-	the x origin of the crop box
	 * @param y				-	the y origin of the crop box
	 * @param width			-	the width of the crop box
	 * @param height		-	the height of the crop box
	 * @param ext			- 	the file extension
	 * @throws IOException
	 */
	public static void cropTIFFImage(BufferedImage bufferedImage,File output, int x, int y, int width, int height, String ext , File image) throws IOException{
		BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
	    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(ext);
        if (!writers.hasNext())
           throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        TIFFImageWriteParam param = new TIFFImageWriteParam(Locale.ENGLISH);;
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        OutputStream os = new FileOutputStream(output);
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	    writer.setOutput(ios);
	    float quality = 0.5f;
	    param.setCompressionType("PackBits");
	    param.setCompressionQuality(quality);  
	    TIFFImageReader reader = (TIFFImageReader) ImageIO.getImageReader(writer);
	    ImageInputStream stream = ImageIO.createImageInputStream(image);
	    reader.setInput(stream);
	    IIOMetadata meta = reader.getImageMetadata(0);
	    writer.write(meta, new IIOImage(croppedImage, null, meta), param);
        ios.close();
        os.close();
        writer.dispose();
        reader.dispose();
	}
	
	
	
	/**
	 * 			method used for testing the parser for computing the coordinates
	 * 			(not deleted for test purposes)
	 * @param words
	 */
	public static void listWords(List<Word> words){
		for(Word w : words){
			System.out.println(w.toString());
			System.out.println();
		}
	}
	
	/**
	 * 			compiles the words in the image file and their coordinates in a list.
	 * @param coordinatesFile	-	is the txt file containing the words and their corresponding coordinates in the image file
	 * @param wordsInImage		-	is the list of word objects that will contain the words
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void getCoordinates(File coordinatesFile,List<Word> wordsInImage, String ext) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(coordinatesFile));
		int minWordLength = Integer.parseInt(AppConfig.MIN_WORD_LENGTH.value());
		String line = null;
		String upperLeftPoint = "";
		String lowerLeftPoint = "";
		String upperRightPoint = "";
		String lowerRightPoint = "";
		String word = "";
		boolean extremesAreNull = (lowestPoint == 0 && highestPoint == 0 && leftMostPoint == 0 && rightMostPoint == 0);
		boolean isPDF = ext.equals("pdf");
		System.out.println("getting coordinate extremities");
		while((line = reader.readLine()) != null){
			String[] wordToInstanceAndCoordinates = line.split(": \\[");
			String[] coordinatesPerWord = {""};
			if(wordToInstanceAndCoordinates.length > 1) coordinatesPerWord = wordToInstanceAndCoordinates[1].replaceAll("\\(1\\)", "").split("\\]\\[");
			word = wordToInstanceAndCoordinates[0];
			boolean wordValid = (word.length() > minWordLength);
			int index = 0;
			if(wordValid){
				for(char c : word.toCharArray()){
					index++;
					if(word.toCharArray().length - 1 == index) break;
					if(!Character.isLetterOrDigit(c)){
						String[] excempted = AppConfig.EXCEMPTED_SYMBOLS.value().split("<@>");
						for(String s : excempted){
							if(s.charAt(0) != c){
								wordValid = false;
								continue;
							}else{
								wordValid = true;
								break;
							}
						}
					}
				}	
//				System.out.println(word);
				for(String l : coordinatesPerWord){
					l = l.replaceAll("]","");
					String[] wordCoordinatesLeftAndRight = l.split("\\),\\(");
					String[] leftCoordinates = wordCoordinatesLeftAndRight[0].replaceAll("\\(", "").split(" : ");
					String[] rightCoordinates = {""};
					if(wordToInstanceAndCoordinates.length > 1) rightCoordinates = wordCoordinatesLeftAndRight[1].replaceAll("\\)", "").split(" : ");
					if(wordToInstanceAndCoordinates.length > 1){
						lowerLeftPoint =  leftCoordinates[0];
						upperLeftPoint = leftCoordinates[1];
						lowerRightPoint = rightCoordinates[0];
						upperRightPoint = rightCoordinates[1];
						String[] leftMostAndHighest = upperLeftPoint.split(" , ");
						String[] rightMostAndLowest = lowerRightPoint.split(" , ");
						if(extremesAreNull){
							leftMostPoint = Double.parseDouble(leftMostAndHighest[0]);	
							highestPoint = Double.parseDouble(leftMostAndHighest[1]);
							rightMostPoint = Double.parseDouble(leftMostAndHighest[1]);
							lowestPoint = Double.parseDouble(rightMostAndLowest[1]);
							extremesAreNull = (lowestPoint == 0 && highestPoint == 0 && leftMostPoint == 0 && rightMostPoint == 0);
						}else if(isPDF){
							leftMostPoint = Math.min(leftMostPoint, Double.parseDouble(leftMostAndHighest[0]));
							lowestPoint = Math.min(lowestPoint, Double.parseDouble(rightMostAndLowest[1]));
							highestPoint = Math.max(highestPoint, Double.parseDouble(leftMostAndHighest[1]));
							rightMostPoint = Math.max(rightMostPoint, Double.parseDouble(rightMostAndLowest[0]));
						}else{
							leftMostPoint = Math.min(leftMostPoint, Double.parseDouble(leftMostAndHighest[0]));
							highestPoint = Math.min(highestPoint, Double.parseDouble(leftMostAndHighest[1]));
							rightMostPoint = Math.max(rightMostPoint, Double.parseDouble(rightMostAndLowest[0]));
							lowestPoint = Math.max(lowestPoint, Double.parseDouble(rightMostAndLowest[1]));
						}
						wordsInImage.add(new Word(word,lowerLeftPoint.split(" , "), upperLeftPoint.split(" , "), 
								lowerRightPoint.split(" , "), upperRightPoint.split(" , ")));
					}
				}
			}
		}
		reader.close();
	}
	
	/**
	 * 			method to compile a list of the input files
	 * @param folder	-	the folder/origin of the input images/coordinate files
	 * @param fileNames	-	the list for compiling the files
	 */
	public static void listFiles(File folder, List<File> fileNames){
		for(File entry : folder.listFiles()){
			if(entry.isDirectory()){
				for(File inner : entry.listFiles()){
					if(inner.isDirectory()){
						for(File inner2 : inner.listFiles()){
							fileNames.add(inner2);
						}
					}else{
						fileNames.add(inner);
					}
				}
			}else{
				fileNames.add(entry);
			}	
		}
	}
	
	/**
	 * 		method to reset the extremes/outermost coordinates
	 * 		used at the end of the for loop when changing the input image
	 */
	public static void resetExtremes(){
		leftMostPoint = 0;
		rightMostPoint = 0;
		highestPoint = 0; 
		lowestPoint = 0;
		croppedHeight = 0;
		croppedWidth = 0;
	}
	
	/**
	 * 			for Adjusting the coordinates of the words in the list based on the movement of the origin
	 * 			when cropping
	 * @param wordList	-	is the list of words found in the image
	 */
	public static void adjustWordCoordinates(List<Word> wordList){
		for(Word w : wordList){
			w.adjustCoordinates(croppedWidth, croppedHeight);
		}
	}
	
	/**
	 * 			for compiling a map so that the words can be printed according to distinctively
	 * 			and containing the coordinates of each of their instance
	 * @param wordsInImage	- the words found in the image
	 * @param wordMap		- the map of <String wordName, List<Word>> to compile the unique words with instances
	 */
	public static void compileWordMap(List<Word> wordsInImage, Map<String, List<Word>> wordMap){
		for(Word word : wordsInImage){
			if(!wordMap.containsKey(word.getWordName())){
				wordMap.put(word.getWordName(), new ArrayList<Word>());
			}
			wordMap.get(word.getWordName()).add(word);
		}
	}
	
	/**
	 * 			method to write the new coordinates file for the cropped output
	 * @param wordMap		-	the map of <String wordName, List<Word>> to compile the unique words with instances
	 * @param output		-	the output file of the coordinates
	 * @throws IOException
	 */
	public static void writeNewCoordinateFile(Map<String,List<Word>> wordMap, File output) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		List<String> coordinatesList = new ArrayList<>();
		for(String s : wordMap.keySet()){
			coordinatesList.add(s + "@ " + wordMap.get(s));
		}
		Collections.sort(coordinatesList, new Comparator<String>(){
			@Override
			public int compare(String s1, String s2) {
		        return s1.compareToIgnoreCase(s2);
		    }
		});
		for(String s : coordinatesList){
			writer.write(s);
			writer.newLine();
		}
		writer.close();
	}
	
	/**
	 * 		method to initialize the config object using the config.properties file
	 */
	private static void initializeConfig() {
		try {
			AppConfig.setContext(new FileInputStream(new File("config/config.properties")));
		} catch (FileNotFoundException e) {
			System.out.println("ConfigFile Not Found");
			e.printStackTrace();
			System.exit(0);
		}

	}
}
