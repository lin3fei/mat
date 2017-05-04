package com.cmcciot.mat.enclosure.model.domain;

import java.io.Serializable;

public class SmsLogsBean implements Serializable
{
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1220156495717948188L;
    
    private String id;
    
    private String createTime;
    
    private String phone;
    
    private String content;
    
    private String sussessFlag;
    
    private String response;
    
    
    
    public String getResponse()
    {
        return response;
    }

    public void setResponse(String response)
    {
        this.response = response;
    }


    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCreateTime()
    {
        return createTime;
    }
    
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }
    
    public String getPhone()
    {
        return phone;
    }
    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getSussessFlag()
    {
        return sussessFlag;
    }
    
    public void setSussessFlag(String sussessFlag)
    {
        this.sussessFlag = sussessFlag;
    }
    
}
