package com.cmcciot.mat.enclosure.model.domain;

import java.io.Serializable;

public class EnClosuerMonitorBean implements Serializable
{
    
    private static final long serialVersionUID = -9122080223455381406L;
    
    private String id;                                 /*围栏Id*/
    
    private String associationdeviceId;                     /*被监控设备Id*/
    
    private String enclosuName;                        /*围栏名称*/
    
    private String enclosuCenter;                      /*围栏圆心*/
    
    private String enclosumapCenter;                   /*百度地图转换围栏中心*/
    
    private int enclosuRadius;                         /*围栏半径*/
    
    private String areaName;                           /*区域名称*/
    
    private String weekTime;                           /*重复时间*/
    
    private String startTime;                          /*开始时间*/
    
    private String endTime;                            /*结束时间*/
    
    private String state;                              /*围栏状态*/
    
    private String pauseState;                         /*暂停状态*/
    
    private String gaodeGps;                           /*是否使用高德GPS*/
   
    private String enclsrfixedshowId;                  /*围栏固定显示ID*/
  
    private String createTime;                         /*创建时间*/
    
    private String enclosuType;                        /*找他 围栏类型  1.进圈  2.出圈*/  
    
    private String monitorId; 
    
    private String deviceId;
    
    private String orgId;
    
    private String enclosu_center_name;
    
	public String getEnclosu_center_name() {
		return enclosu_center_name;
	}

	public void setEnclosu_center_name(String enclosu_center_name) {
		this.enclosu_center_name = enclosu_center_name;
	}

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getAssociationdeviceId() {
		return associationdeviceId;
	}

	public void setAssociationdeviceId(String associationdeviceId) {
		this.associationdeviceId = associationdeviceId;
	}

	public String getEnclosumapCenter() {
		return enclosumapCenter;
	}

	public void setEnclosumapCenter(String enclosumapCenter) {
		this.enclosumapCenter = enclosumapCenter;
	}

	public String getEnclosuType() {
		return enclosuType;
	}

	public void setEnclosuType(String enclosuType) {
		this.enclosuType = enclosuType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getGaodeGps() {
		return gaodeGps;
	}

	public void setGaodeGps(String gaodeGps) {
		this.gaodeGps = gaodeGps;
	}

	public String getEnclsrfixedshowId() {
		return enclsrfixedshowId;
	}

	public void setEnclsrfixedshowId(String enclsrfixedshowId) {
		this.enclsrfixedshowId = enclsrfixedshowId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

   
}
