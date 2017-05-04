/*
 * 文 件 名:  SpuerBackInfo.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  lidechun
 * 修改时间:  2014年5月5日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.sdhttp.bean;

import java.io.Serializable;

import net.sf.json.JSONObject;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  lidechun
 * @version  [版本号, 2014年5月5日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class OneNetBackInfo implements Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求http返回结果的编码
     */
    private int errno;
    
    /**
     * 请求http返回结果描述信息
     */
    private String error;
    
    private JSONObject data;
    

    
    
    /**
     * @return 返回 data
     */
    public JSONObject getData()
    {
        return data;
    }



    /**
     * @param 对data进行赋值
     */
    public void setData(JSONObject data)
    {
        this.data = data;
    }



    /**
     * @return 返回 errno
     */
    public int getErrno()
    {
        return errno;
    }
    
    

    /**
     * @param 对errno进行赋值
     */
    public void setErrno(int errno)
    {
        this.errno = errno;
    }

    /**
     * @return 返回 error
     */
    public String getError()
    {
        return error;
    }

    /**
     * @param 对error进行赋值
     */
    public void setError(String error)
    {
        this.error = error;
    }












    
    
   
}
