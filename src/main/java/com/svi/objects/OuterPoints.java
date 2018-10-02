package com.svi.objects;

public class OuterPoints {
	private double leftMostPoint;
	private double rightMostPoint;
	private double highestPoint;
	private double lowestPoint;
	
	public OuterPoints(double left, double right, double top, double bottom){
		this.leftMostPoint = left;
		this.rightMostPoint = right;
		this.highestPoint = top;
		this.lowestPoint = bottom;
	}

	public double getLeftMostPoint() {
		return leftMostPoint;
	}

	public void setLeftMostPoint(double leftMostPoint) {
		this.leftMostPoint = leftMostPoint;
	}

	public double getRightMostPoint() {
		return rightMostPoint;
	}

	public void setRightMostPoint(double rightMostPoint) {
		this.rightMostPoint = rightMostPoint;
	}

	public double getHighestPoint() {
		return highestPoint;
	}

	public void setHighestPoint(double highestPoint) {
		this.highestPoint = highestPoint;
	}

	public double getLowestPoint() {
		return lowestPoint;
	}

	public void setLowestPoint(double lowestPoint) {
		this.lowestPoint = lowestPoint;
	}
	
	
}
