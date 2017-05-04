package com.cmcciot.mat.enclosure.model.domain;

import java.io.Serializable;

public class EnclosureDeviceHistorydatastreamBean implements Serializable
{

    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    private String  deviceid;
    private String  longitude;
    private String  latitude;
    private String  updatedate;
    private String  enclosuid;
    private String  alarmdate;
    /**
     * @return 返回 deviceid
     */
    public String getDeviceid()
    {
        return deviceid;
    }
    /**
     * @param 对deviceid进行赋值
     */
    public void setDeviceid(String deviceid)
    {
        this.deviceid = deviceid;
    }
    /**
     * @return 返回 longitude
     */
    public String getLongitude()
    {
        return longitude;
    }
    /**
     * @param 对longitude进行赋值
     */
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
    /**
     * @return 返回 latitude
     */
    public String getLatitude()
    {
        return latitude;
    }
    /**
     * @param 对latitude进行赋值
     */
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }
    /**
     * @return 返回 updatedate
     */
    public String getUpdatedate()
    {
        return updatedate;
    }
    /**
     * @param 对updatedate进行赋值
     */
    public void setUpdatedate(String updatedate)
    {
        this.updatedate = updatedate;
    }
    /**
     * @return 返回 enclosuid
     */
    public String getEnclosuid()
    {
        return enclosuid;
    }
    /**
     * @param 对enclosuid进行赋值
     */
    public void setEnclosuid(String enclosuid)
    {
        this.enclosuid = enclosuid;
    }
    /**
     * @return 返回 alarmdate
     */
    public String getAlarmdate()
    {
        return alarmdate;
    }
    /**
     * @param 对alarmdate进行赋值
     */
    public void setAlarmdate(String alarmdate)
    {
        this.alarmdate = alarmdate;
    }
    
    

}
