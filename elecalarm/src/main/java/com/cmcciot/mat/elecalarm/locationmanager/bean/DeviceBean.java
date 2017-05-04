/*
 * 文 件 名:  EqptInfoBean.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月3日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.locationmanager.bean;

import java.io.Serializable;

/**
 * <设备实体类>
 * 
 * @author Administrator
 * @version [版本号, 2014年4月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DeviceBean implements Serializable {
	/**
	 * 注释内容
	 */
	private static final long serialVersionUID = 1L;

	// 设备ID
	private String id;

	// 设备名称
	private String device_name;

	// 监控对象ID
	private Integer monitor_id;

	// 设备手机号
	private String device_phone_no;

	// 设备类型，1：脸蛋 2：宝儿
	private Integer device_type;

	// 设备IMEI
	private String device_IMEI;

	// 设备描述
	private String device_desc;

	// 设备云分配的ID
	private String sd_device_id;

	// 设备云分配的key
	private String sd_device_key;

	// 数据流
	private String datastreams;

	// 创建时间
	private String createTime;

	// 激活时间
	private String activateTime;

	// 冗余字段（查询）
	private String[] deviceIDs;

	// 冗余字段（查询）
	private String[] monitorIDs;

	// 冗余字段（查询）
	private String user_name;

	// 设备状态
	private Integer device_status;

	// 设备SIM卡唯一标识
	private String device_imsi;

	// 设备厂家字段
	private String device_vender;

	// 设备型号
	private String device_model;

	// 设备spno
	private String spno;

	// 设备连接云端地址
	private String sdaddr;

	// 设备连接云端端口
	private String sdport;

	// 设备当前使用的协议版本
	private Integer pver;

	// 设备期望使用的协议类型
	private Integer prtc;
	
	//设备硬件版本号
    private String hdver;
    
	// 设备固件版本号
	private String fwver;

	// 省份编码
	private String provinceCode;

	// 城市编码
	private String cityCode;
	// 设备SN号
	private String device_sn;
	// 绑定时间
	private String bandtime;
	// 是否绑定 1未绑定，2绑定
	private String isband;
	// 设备上传数据频率
	private String freq;
	
	//设备开机状态(0:开机,1:关机)
    private String switchState;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSd_device_id() {
		return sd_device_id;
	}

	public void setSd_device_id(String sd_device_id) {
		this.sd_device_id = sd_device_id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public Integer getMonitor_id() {
		return monitor_id;
	}

	public void setMonitor_id(Integer monitor_id) {
		this.monitor_id = monitor_id;
	}

	public String getDevice_phone_no() {
		return device_phone_no;
	}

	public void setDevice_phone_no(String device_phone_no) {
		this.device_phone_no = device_phone_no;
	}

	public Integer getDevice_type() {
		return device_type;
	}

	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}

	public String getDevice_IMEI() {
		return device_IMEI;
	}

	public void setDevice_IMEI(String device_IMEI) {
		this.device_IMEI = device_IMEI;
	}

	public String getDevice_desc() {
		return device_desc;
	}

	public void setDevice_desc(String device_desc) {
		this.device_desc = device_desc;
	}

	public String getSd_device_key() {
		return sd_device_key;
	}

	public void setSd_device_key(String sd_device_key) {
		this.sd_device_key = sd_device_key;
	}

	/**
	 * @return 返回 deviceIDs
	 */
	public String[] getDeviceIDs() {
		return deviceIDs;
	}

	/**
	 * @param 对deviceIDs进行赋值
	 */
	public void setDeviceIDs(String[] deviceIDs) {
		this.deviceIDs = deviceIDs;
	}

	/**
	 * @return 返回 monitorIDs
	 */
	public String[] getMonitorIDs() {
		return monitorIDs;
	}

	/**
	 * @param 对monitorIDs进行赋值
	 */
	public void setMonitorIDs(String[] monitorIDs) {
		this.monitorIDs = monitorIDs;
	}

	/**
	 * @return 返回 datastreams
	 */
	public String getDatastreams() {
		return datastreams;
	}

	/**
	 * @param 对datastreams进行赋值
	 */
	public void setDatastreams(String datastreams) {
		this.datastreams = datastreams;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getActivateTime() {
		return activateTime;
	}

	public void setActivateTime(String activateTime) {
		this.activateTime = activateTime;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getDevice_status() {
		return device_status;
	}

	public void setDevice_status(Integer device_status) {
		this.device_status = device_status;
	}

	public String getDevice_imsi() {
		return device_imsi;
	}

	public void setDevice_imsi(String device_imsi) {
		this.device_imsi = device_imsi;
	}

	public String getDevice_vender() {
		return device_vender;
	}

	public void setDevice_vender(String device_vender) {
		this.device_vender = device_vender;
	}

	public String getDevice_model() {
		return device_model;
	}

	public void setDevice_model(String device_model) {
		this.device_model = device_model;
	}

	public String getSpno() {
		return spno;
	}

	public void setSpno(String spno) {
		this.spno = spno;
	}

	public String getSdaddr() {
		return sdaddr;
	}

	public void setSdaddr(String sdaddr) {
		this.sdaddr = sdaddr;
	}

	public String getSdport() {
		return sdport;
	}

	public void setSdport(String sdport) {
		this.sdport = sdport;
	}

	public Integer getPver() {
		return pver;
	}

	public void setPver(Integer pver) {
		this.pver = pver;
	}

	public Integer getPrtc() {
		return prtc;
	}

	public void setPrtc(Integer prtc) {
		this.prtc = prtc;
	}

	public String getFwver() {
		return fwver;
	}

	public void setFwver(String fwver) {
		this.fwver = fwver;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getDevice_sn() {
		return device_sn;
	}

	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}

	public String getBandtime() {
		return bandtime;
	}

	public void setBandtime(String bandtime) {
		this.bandtime = bandtime;
	}

	public String getIsband() {
		return isband;
	}

	public void setIsband(String isband) {
		this.isband = isband;
	}

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

    public String getHdver()
    {
        return hdver;
    }

    public void setHdver(String hdver)
    {
        this.hdver = hdver;
    }

	public String getSwitchState() {
		return switchState;
	}

	public void setSwitchState(String switchState) {
		this.switchState = switchState;
	}
	
	

}
