package com.svi.objects;

public class Word {
	private String wordName;
	private String upperLeftCoordinate;
	private String lowerLeftCoordinate;
	private String upperRightCoordinate;
	private String lowerRightCoordinate;
	
	public Word(String wordName,String LLCoordinate, String ULCoordinate, String LRCoordinate, String URCoordinate){
		this.wordName = wordName;
		this.upperLeftCoordinate = ULCoordinate;
		this.lowerLeftCoordinate = LLCoordinate;
		this.upperRightCoordinate = URCoordinate;
		this.lowerRightCoordinate = LRCoordinate;
	}
	
	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	public String getUpperLeftCoordinate() {
		return upperLeftCoordinate;
	}

	public void setUpperLeftCoordinate(String upperLeftCoordinate) {
		this.upperLeftCoordinate = upperLeftCoordinate;
	}

	public String getLowerLeftCoordinate() {
		return lowerLeftCoordinate;
	}

	public void setLowerLeftCoordinate(String lowerLeftCoordinate) {
		this.lowerLeftCoordinate = lowerLeftCoordinate;
	}

	public String getUpperRightCoordinate() {
		return upperRightCoordinate;
	}

	public void setUpperRightCoordinate(String upperRightCoordinate) {
		this.upperRightCoordinate = upperRightCoordinate;
	}

	public String getLowerRightCoordinate() {
		return lowerRightCoordinate;
	}

	public void setLowerRightCoordinate(String lowerRightCoordinate) {
		this.lowerRightCoordinate = lowerRightCoordinate;
	}
	
	public String toString(){
		return "The word is : \n" + this.wordName + "\nThe left coordinates are : \n" + 
				"LL point is " + this.upperLeftCoordinate + " : UL point is " + this.lowerLeftCoordinate + "\nThe right coordinates are : \n"
				+ "LR point is " + this.upperRightCoordinate + " : UR point is " + this.lowerRightCoordinate;		
	}
	
	
	

}
