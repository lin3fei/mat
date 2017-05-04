package com.cmcciot.mat.elecalarm.alarm.bean;

/**
 * 
 * <用于存放围栏缓存信息>
 * <功能详细描述>
 * 
 * @author  weilei
 * @version  [版本号, 2015年8月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class CacheObjectBean
{
    /**
     * 围栏ID
     */
    private int encloId;
    
    /**
     * 上一次触发告警数据点的时间
     */
    private String lastDataTime;
    /**
     * 上一次告警时间
     */
    private long lastAlarmTime;
    
    public int getEncloId()
    {
        return encloId;
    }
    
    public void setEncloId(int encloId)
    {
        this.encloId = encloId;
    }
    
    public String getLastDataTime()
    {
        return lastDataTime;
    }
    
    public void setLastDataTime(String lastDataTime)
    {
        this.lastDataTime = lastDataTime;
    }
    
    public long getLastAlarmTime()
    {
        return lastAlarmTime;
    }
    
    public void setLastAlarmTime(long lastAlarmTime)
    {
        this.lastAlarmTime = lastAlarmTime;
    }
    
}
