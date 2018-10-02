package com.svi.cropping_tool;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.svi.objects.Word;



public class CroppingTool {
	private static double leftMostPoint = 9999;
	private static double rightMostPoint = 0;
	private static double highestPoint = 9999;
	private static double lowestPoint = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		initializeConfig();
		File imageInputFolder = new File(AppConfig.IMAGE_INPUT_PATH.value());
		File coordinateInputFolder = new File(AppConfig.COORDINATE_INPUT_PATH.value());
		//for compiling the inputs into a list
		List<File> fileImages = new ArrayList<>();
		List<File> fileCoordinates = new ArrayList<>();
		listFiles(imageInputFolder, fileImages);
		listFiles(coordinateInputFolder, fileCoordinates);
		
		
		for(File image : fileImages){
			if(image.getName().substring(image.getName().length() - 3).equalsIgnoreCase("jpg")){
				String imageName = image.getName().substring(0, image.getName().length() - 4);
				System.out.println("Processing " + imageName);
					for(File coordinate : fileCoordinates){
						String coordinateName = coordinate.getName().substring(0, coordinate.getName().length() - 4);
						if(coordinateName.equalsIgnoreCase(imageName + "_jpg")){
							System.out.println("Using coordinate file " + coordinateName);
							String outputName = image.getName().substring(0, image.getName().length() - 4) + "_cropped.jpg";
							File croppedImageOutputFolder = new File(image.getParentFile().getAbsolutePath().replaceAll("input", "cropped images"));
							if(!croppedImageOutputFolder.exists())croppedImageOutputFolder.mkdirs();
							File testOuputImage = new File(croppedImageOutputFolder + "/" + outputName);
							getCoordinates(coordinate);
							BufferedImage test = ImageIO.read(image);
							cropImage(test, testOuputImage);
							System.out.println();
							System.out.println("done writing " + outputName);
							System.out.println();
							break;
						}
					}
					resetExtremes();
			}
		}
	}
	
	public static void cropImage(BufferedImage bufferedImage, File output) throws IOException{
		double heightOrigin = bufferedImage.getHeight();
		double widthOrigin = bufferedImage.getWidth();
		double heightDiff = bufferedImage.getHeight() * 0.0001;
		double widthDiff = bufferedImage.getWidth() * 0.0001;
		int x = (int) (leftMostPoint - widthDiff) + 1;
		int y = (int) (highestPoint - heightDiff) + 1;
		int width = (int) (rightMostPoint - leftMostPoint + widthDiff) + 1;
		int height = (int) (lowestPoint - highestPoint + heightDiff) + 1;
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		if(width > widthOrigin) width = (int) widthOrigin;
		if(height > heightOrigin) height = (int) heightOrigin;
		if(y > 0 && (y + height) > heightOrigin){
			y = (int) Math.abs((y + height) - (y + heightOrigin));
		}
		if(x > 0 && (x + width) > widthOrigin){
			x = (int) Math.abs((x + width) - (x + widthOrigin));
		}
		System.out.println("The crop will start at x coordinate " + x + " y coordinate " + y);
		System.out.println("The width margin will be : " + widthDiff);
		System.out.println("The height margin will be : " + heightDiff);
		System.out.println("The cropped image width is " + (width));
		System.out.println("The cropped image height is " + (height));
		System.out.println("The original width is " + widthOrigin);
		System.out.println("The original height is " + heightOrigin);
		System.out.println("Writing cropped image to file...");
		System.out.println(output.getAbsolutePath());
	    BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
	    ImageIO.write(croppedImage, "jpg", output);
	}
	
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
	public static void getCoordinates(File coordinatesFile) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(coordinatesFile));
		String line = null;
		String upperLeftPoint = "";
		String lowerLeftPoint = "";
		String upperRightPoint = "";
		String lowerRightPoint = "";
		String word = "";
		System.out.println("getting coordinate extremities");
		while((line = reader.readLine()) != null){
			String[] wordToInstanceAndCoordinates = line.split(": \\[");
			String[] coordinatesPerWord = {""};
			if(wordToInstanceAndCoordinates.length > 1) coordinatesPerWord = wordToInstanceAndCoordinates[1].replaceAll("\\(1\\)", "").split("\\]\\[");
			word = wordToInstanceAndCoordinates[0];
			if(word.length() > 3 && !word.contains("?") && !word.contains("�")){
//				System.out.println(word);
				for(String l : coordinatesPerWord){
					l = l.replaceAll("]","");
					String[] wordCoordinatesLeftAndRight = l.split("\\),\\(");
					String[] leftCoordinates = wordCoordinatesLeftAndRight[0].replaceAll("\\(", "").split(" : ");
					String[] rightCoordinates = {""};
					if(wordToInstanceAndCoordinates.length > 1) rightCoordinates = wordCoordinatesLeftAndRight[1].replaceAll("\\)", "").split(" : ");
					if(wordToInstanceAndCoordinates.length > 1){
						lowerLeftPoint = leftCoordinates[0];
						upperLeftPoint = leftCoordinates[1];
						lowerRightPoint = rightCoordinates[0];
						upperRightPoint = rightCoordinates[1];
						String[] leftMostAndHighest = upperLeftPoint.split(" , ");
						String[] rightMostAndLowest = lowerRightPoint.split(" , ");
						leftMostPoint = Math.min(leftMostPoint, Double.parseDouble(leftMostAndHighest[0]));
						highestPoint = Math.min(highestPoint, Double.parseDouble(leftMostAndHighest[1]));
						rightMostPoint = Math.max(rightMostPoint, Double.parseDouble(rightMostAndLowest[0]));
						lowestPoint = Math.max(lowestPoint, Double.parseDouble(rightMostAndLowest[1]));
					}
				}
			}
		}
		reader.close();
	}
	
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
	
	public static void resetExtremes(){
		leftMostPoint = 9999;
		rightMostPoint = 0;
		highestPoint = 9999; 
		lowestPoint = 0;
	}
	
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
