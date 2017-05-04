package com.cmcciot.mat.enclosure.model.domain;

import java.io.Serializable;

public class AlertMessageBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    //主键ID
    private String alertId;
    
    //设备ID
    private String alertDeviceId;
    
    //产生告警时间
    private String alertTime;
    
    //保存时间
    private String saveTime;
    
    //类型
    private String alertIndex;
    
    //现在值
    private String alertValue;
    
    //门限值
    private String alertThreshold;
    
    //条件
    private String alertCondition;
    
    //告警类容
    private String alertInfo;
    
    // 终端上传告警ID
    private String sdAlertId;
    
    // 录音索引
    private String recordIndex;
    
    // 围栏ID
    private int enclosureId;
    
    public String getAlertId()
    {
        return alertId;
    }
    
    public void setAlertId(String alertId)
    {
        this.alertId = alertId;
    }
    
    public String getAlertDeviceId()
    {
        return alertDeviceId;
    }
    
    public void setAlertDeviceId(String alertDeviceId)
    {
        this.alertDeviceId = alertDeviceId;
    }
    
    public String getAlertTime()
    {
        return alertTime;
    }
    
    public void setAlertTime(String alertTime)
    {
        this.alertTime = alertTime;
    }
    
    public String getSaveTime()
    {
        return saveTime;
    }
    
    public void setSaveTime(String saveTime)
    {
        this.saveTime = saveTime;
    }
    
    public String getAlertIndex()
    {
        return alertIndex;
    }
    
    public void setAlertIndex(String alertIndex)
    {
        this.alertIndex = alertIndex;
    }
    
    public String getAlertValue()
    {
        return alertValue;
    }
    
    public void setAlertValue(String alertValue)
    {
        this.alertValue = alertValue;
    }
    
    public String getAlertThreshold()
    {
        return alertThreshold;
    }
    
    public void setAlertThreshold(String alertThreshold)
    {
        this.alertThreshold = alertThreshold;
    }
    
    public String getAlertCondition()
    {
        return alertCondition;
    }
    
    public void setAlertCondition(String alertCondition)
    {
        this.alertCondition = alertCondition;
    }
    
    public String getAlertInfo()
    {
        return alertInfo;
    }
    
    public void setAlertInfo(String alertInfo)
    {
        this.alertInfo = alertInfo;
    }

	public String getSdAlertId() {
		return sdAlertId;
	}

	public void setSdAlertId(String sdAlertId) {
		this.sdAlertId = sdAlertId;
	}

	public String getRecordIndex() {
		return recordIndex;
	}

	public void setRecordIndex(String recordIndex) {
		this.recordIndex = recordIndex;
	}

	public int getEnclosureId() {
		return enclosureId;
	}

	public void setEnclosureId(int enclosureId) {
		this.enclosureId = enclosureId;
	}
    
}
