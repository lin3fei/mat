package com.cmcciot.mat.elecalarm.alarm.bean;

import java.io.Serializable;

public class EncloAlarmBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 告警ID
	 */
	private int id;
	
	/**
	 * 围栏ID
	 */
	private int encloId;
	
	/**
	 * 告警次数
	 */
	private int alarmTimes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEncloId() {
		return encloId;
	}

	public void setEncloId(int encloId) {
		this.encloId = encloId;
	}

	public int getAlarmTimes() {
		return alarmTimes;
	}

	public void setAlarmTimes(int alarmTimes) {
		this.alarmTimes = alarmTimes;
	}
}
