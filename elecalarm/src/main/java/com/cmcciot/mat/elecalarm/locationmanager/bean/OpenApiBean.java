package com.cmcciot.mat.elecalarm.locationmanager.bean;

import java.io.Serializable;

public class OpenApiBean implements Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 服务ID
     */
    private String serverID;
    
    /**
     * 服务密码
     */
    private String password;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 响应地址
     */
    private String url;
    
    /**
     * FTP登录用户
     */
    private String ftpLoginName;
    
    /**
     * FTP登录密码
     */
    private String ftpLoginPwd;
    
    /**
     * FTP端口
     */
    private String ftpPort;
    
    /**
     * FTP图片验证码路径
     */
    private String ftpImagePath;
    
    public OpenApiBean()
    {
    }
    
    public OpenApiBean(String serverID, String password, String ip, String url)
    {
        super();
        this.serverID = serverID;
        this.password = password;
        this.ip = ip;
        this.url = url;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getServerID()
    {
        return serverID;
    }
    
    public void setServerID(String serverID)
    {
        this.serverID = serverID;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getFtpLoginName()
    {
        return ftpLoginName;
    }
    
    public void setFtpLoginName(String ftpLoginName)
    {
        this.ftpLoginName = ftpLoginName;
    }
    
    public String getFtpLoginPwd()
    {
        return ftpLoginPwd;
    }
    
    public void setFtpLoginPwd(String ftpLoginPwd)
    {
        this.ftpLoginPwd = ftpLoginPwd;
    }
    
    public String getFtpPort()
    {
        return ftpPort;
    }
    
    public void setFtpPort(String ftpPort)
    {
        this.ftpPort = ftpPort;
    }

    public String getFtpImagePath()
    {
        return ftpImagePath;
    }

    public void setFtpImagePath(String ftpImagePath)
    {
        this.ftpImagePath = ftpImagePath;
    }
}
