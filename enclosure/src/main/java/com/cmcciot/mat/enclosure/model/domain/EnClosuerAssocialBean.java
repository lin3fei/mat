package com.cmcciot.mat.enclosure.model.domain;

public class EnClosuerAssocialBean {
	
    private Integer id;                      

    private String deviceId;                  /*设备ID*/

    private String monitorId;                 /*监控对象ID*/

    private String org_id;                    /*组ID*/

    private String enclosuId;
    
	public String getEnclosuId() {
		return enclosuId;
	}

	public void setEnclosuId(String enclosuId) {
		this.enclosuId = enclosuId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

}