package com.cmcciot.mat.elecalarm.locationmanager.bean;

public class LatLng {
	private double lat;
	private double lng;

	public LatLng(double lat,double lng){
		this.lat = lat;
		this.lng = lng;
	}
	public double getLat(){
		return this.lat;
	}
	public double getLng(){
		return this.lng;
	}
}
