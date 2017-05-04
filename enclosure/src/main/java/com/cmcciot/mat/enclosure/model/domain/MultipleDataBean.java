package com.cmcciot.mat.enclosure.model.domain;

import java.io.Serializable;

public class MultipleDataBean implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    private int id;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 数据类型
     */
    private String type;
    
    /**
     * 创建时间
     */
    private String time;
    
    /**
     * 数据
     */
    private String data;
    
    /**
     * 围栏ID
     */
    private int enclosureId;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getTime()
    {
        return time;
    }
    
    public void setTime(String time)
    {
        this.time = time;
    }
    
    public String getData()
    {
        return data;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
    
    public int getEnclosureId()
    {
        return enclosureId;
    }
    
    public void setEnclosureId(int enclosureId)
    {
        this.enclosureId = enclosureId;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
}
