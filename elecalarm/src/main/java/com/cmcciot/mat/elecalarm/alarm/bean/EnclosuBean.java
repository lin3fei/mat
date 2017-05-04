package com.cmcciot.mat.elecalarm.alarm.bean;

import java.io.Serializable;

public class EnclosuBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 围栏ID
	 */
	private int id;
	
	/**
	 * 设备ID
	 */
	private int deviceId;
	
	/**
	 * 围栏名称
	 */
	private String enclosuName;
	
	/**
	 * 围栏圆心
	 */
	private String enclosuCenter;
	
	/**
	 * 围栏半径
	 */
	private int enclosuRadius;
	
	/**
	 * 区域名称
	 */
	private String areaName;
	
	/**
	 * 重复时间
	 */
	private String weekTime;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 围栏状态
	 */
	private String state;
	
	/**
	 * 暂停状态
	 */
	private String pauseState;
	
	private String gaodeGps;

	private String createTime;

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getGaodeGps() {
		return gaodeGps;
	}

	public void setGaodeGps(String gaodeGps) {
		this.gaodeGps = gaodeGps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getEnclosuName() {
		return enclosuName;
	}

	public void setEnclosuName(String enclosuName) {
		this.enclosuName = enclosuName;
	}

	public String getEnclosuCenter() {
		return enclosuCenter;
	}

	public void setEnclosuCenter(String enclosuCenter) {
		this.enclosuCenter = enclosuCenter;
	}

	public int getEnclosuRadius() {
		return enclosuRadius;
	}

	public void setEnclosuRadius(int enclosuRadius) {
		this.enclosuRadius = enclosuRadius;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getWeekTime() {
		return weekTime;
	}

	public void setWeekTime(String weekTime) {
		this.weekTime = weekTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPauseState() {
		return pauseState;
	}

	public void setPauseState(String pauseState) {
		this.pauseState = pauseState;
	}
}
