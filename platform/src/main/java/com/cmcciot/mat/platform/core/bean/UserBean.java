package com.cmcciot.mat.platform.core.bean;

import java.io.Serializable;

public class UserBean implements Serializable
{
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    private String id;
    
    /**
     * 用户名（登录名、唯一）
     */
    private String userName;
    
    /**
     * 用户名
     */
    private String userNickname;
    
    /**
     * 密码
     */
    private String userKey;
    
    /**
     * 手机号
     */
    private String userPhoneNo;
    
    /**
     * 用户头像
     */
    private String userPhoto;
    
    /**
     * 注册时间
     */
    private String registerTime;
    
    /**
     * 用户登录使用的手机imei
     */
    private String imei;
    
    /**
     * 用户登录的位置
     */
    private String currentLocation;
    /**
     * 城市
     */
    private String city;
    /**
     * 登录时间
     */
    private String loginTime;
    /**
     * 省份
     */
    private String province;
    /**
     * 第三方平台uid
     */
    private String uidOpenPlatform;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 是否boss用户
     */
    private String isBoss;
    /**
     * 省boss名
     */
    private String bossName;
    /**
     * 同步到春雨平台标志
     */
    
    private String cyFlag;
    
    /**
     * 平台来源
     */
    private String appType;
    
    /**
     * 用户状态
     * */
    private int status;
    /**
     * 用户是否同步到攀枝花和健康
     * */
    private int isSynToPZH;
    
    /**
     * 是否是行业用户,0表示是，其他表示不是
     */
    private String isHy;
    /**
     * 行业组织id
     */
    private String hyId;
    
	public int getIsSynToPZH() {
		return isSynToPZH;
	}

	public void setIsSynToPZH(int isSynToPZH) {
		this.isSynToPZH = isSynToPZH;
	}

	public String getAppType()
    {
        return appType;
    }

    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public String getCyFlag()
    {
        return cyFlag;
    }

    public void setCyFlag(String cyFlag)
    {
        this.cyFlag = cyFlag;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(String loginTime)
    {
        this.loginTime = loginTime;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getUidOpenPlatform()
    {
        return uidOpenPlatform;
    }

    public void setUidOpenPlatform(String uidOpenPlatform)
    {
        this.uidOpenPlatform = uidOpenPlatform;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserPhoneNo() {
		return userPhoneNo;
	}

	public void setUserPhoneNo(String userPhoneNo) {
		this.userPhoneNo = userPhoneNo;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	public String getIsBoss(){
		return isBoss;
	}
	public void setIsBoss(String isBoss){
		this.isBoss = isBoss;
	}
  
	public String getBossName(){
		return bossName;
	}
	public void setBossName(String bossName){
		this.bossName = bossName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public String getIsHy()
    {
        return isHy;
    }

    public void setIsHy(String isHy)
    {
        this.isHy = isHy;
    }

    public String getHyId()
    {
        return hyId;
    }

    public void setHyId(String hyId)
    {
        this.hyId = hyId;
    }

}
