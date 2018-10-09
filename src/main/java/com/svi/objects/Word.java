package com.svi.objects;

public class Word {
	private String wordName;
	private int[] upperLeftCoordinate;
	private int[] lowerLeftCoordinate;
	private int[] upperRightCoordinate;
	private int[] lowerRightCoordinate;
	
	public Word(String wordName,String[] LLCoordinate, String[] ULCoordinate, String[] LRCoordinate, String[] URCoordinate){
		this.wordName = wordName;
		this.upperLeftCoordinate = StringArrToIntArr(ULCoordinate);
		this.lowerLeftCoordinate = StringArrToIntArr(LLCoordinate);
		this.upperRightCoordinate = StringArrToIntArr(URCoordinate);
		this.lowerRightCoordinate = StringArrToIntArr(LRCoordinate);
	}
	
	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	public int[] getUpperLeftCoordinate() {
		return upperLeftCoordinate;
	}

	public void setUpperLeftCoordinate(int[] upperLeftCoordinate) {
		this.upperLeftCoordinate = upperLeftCoordinate;
	}

	public int[] getLowerLeftCoordinate() {
		return lowerLeftCoordinate;
	}

	public void setLowerLeftCoordinate(int[] lowerLeftCoordinate) {
		this.lowerLeftCoordinate = lowerLeftCoordinate;
	}

	public int[] getUpperRightCoordinate() {
		return upperRightCoordinate;
	}

	public void setUpperRightCoordinate(int[] upperRightCoordinate) {
		this.upperRightCoordinate = upperRightCoordinate;
	}

	public int[] getLowerRightCoordinate() {
		return lowerRightCoordinate;
	}

	public void setLowerRightCoordinate(int[] lowerRightCoordinate) {
		this.lowerRightCoordinate = lowerRightCoordinate;
	}
	
	public String toString(){
		return "[" + coordinateToString(this.lowerLeftCoordinate) + coordinateToString(this.upperLeftCoordinate) 
				+ coordinateToString(this.lowerRightCoordinate) + coordinateToString(this.upperRightCoordinate) + "]";		
	}
	
	public void adjustCoordinates(int x, int y){	
		
		this.upperLeftCoordinate[0] -= x;
		this.upperLeftCoordinate[0] = (this.upperLeftCoordinate[0] < 0) ? 0:this.upperLeftCoordinate[0];
		this.lowerLeftCoordinate[0] -= x; 
		this.lowerLeftCoordinate[0] = (this.lowerLeftCoordinate[0] < 0) ? 0:this.lowerLeftCoordinate[0];
		this.upperRightCoordinate[0] -= x; 
		this.upperRightCoordinate[0] = (this.upperRightCoordinate[0] < 0) ? 0:this.upperRightCoordinate[0];
		this.lowerRightCoordinate[0] -= x; 
		this.lowerRightCoordinate[0] = (this.lowerRightCoordinate[0] < 0) ? 0:this.lowerRightCoordinate[0];
		this.upperLeftCoordinate[1] -= y;
		this.upperLeftCoordinate[0] = (this.upperLeftCoordinate[0] < 0) ? 0:this.upperLeftCoordinate[0];
		this.lowerLeftCoordinate[1] -= y; 
		this.lowerLeftCoordinate[0] = (this.lowerLeftCoordinate[0] < 0) ? 0:this.lowerLeftCoordinate[0];
		this.upperRightCoordinate[1] -= y; 
		this.upperRightCoordinate[0] = (this.upperRightCoordinate[0] < 0) ? 0:this.upperRightCoordinate[0];
		this.lowerRightCoordinate[1] -= y; 
		this.lowerRightCoordinate[0] = (this.lowerRightCoordinate[0] < 0) ? 0:this.lowerRightCoordinate[0];
		
		
		
	}
	
	public static int[] StringArrToIntArr(String[] s) {
		   int[] result = new int[s.length];
		   for (int i = 0; i < s.length; i++) {
		      result[i] = (int)Double.parseDouble(s[i]);
		   }
		   return result;
	}
	
	public static String coordinateToString(int[] coordinate){
		String out = "(";
		int x = 0;
		for(int i : coordinate){
			out += i;
			out = (x == 1) ? (out + ")") : (out + " : "); 
			x++;
		}
		return out;
	}

}
