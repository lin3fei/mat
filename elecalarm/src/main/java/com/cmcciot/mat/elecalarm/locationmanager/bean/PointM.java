package com.cmcciot.mat.elecalarm.locationmanager.bean;

public class PointM {
	private double x;
	private double y;
	private double offsetlat;
	private double offsetlng;

	public PointM(){
	}
	public PointM(double x,double y){
		this.x = x;
		this.y = y;
	}

	public double getx(){
		return this.x;
	}
	public void setx(double x)
	{
		this.x = x;
	}

	public double gety(){
		return this.y;
	}
	public void sety(double y){
		this.y = y;
	}

	public double getOffsetlat() {
		return offsetlat;
	}

	public void setOffsetlat(double offsetlat) {
		this.offsetlat = offsetlat;
	}

	public double getOffsetlng() {
		return offsetlng;
	}

	public void setOffsetlng(double offsetlng) {
		this.offsetlng = offsetlng;
	}
	
}
