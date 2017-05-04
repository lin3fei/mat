package com.cmcciot.mat.elecalarm.alarm.bean;

import java.io.Serializable;

public class EnclosuLogBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 日志ID
	 */
	private int id;
	
	/**
	 * 围栏ID
	 */
	private int enclosuId;
	
	/**
	 * 设备ID
	 */
	private String deviceId;
	
	/**
	 * 告警信息
	 */
	private String alarmInfo;
	
	/**
	 * 告警次数
	 */
	private String alarmTimes;
	
	/**
	 * 创建时间
	 */
	private String creatTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEnclosuId() {
		return enclosuId;
	}

	public void setEnclosuId(int enclosuId) {
		this.enclosuId = enclosuId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public String getAlarmTimes() {
		return alarmTimes;
	}

	public void setAlarmTimes(String alarmTimes) {
		this.alarmTimes = alarmTimes;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}
	
}
