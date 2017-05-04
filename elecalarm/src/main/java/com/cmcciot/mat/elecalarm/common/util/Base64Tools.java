/*
 * 文 件 名:  Base64Tools.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  lidechun
 * 修改时间:  2014年4月15日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  lidechun
 * @version  [版本号, 2014年4月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Base64Tools
{
    /**
     * <BASE64加密>
     * <功能详细描述>
     * @param content 需要加密的内容
     * @return 加密结果
     * 
     */
    public static String encrypt(String content)
    {
        // 加密后字符串
        String result = null;
        
        // 如果输入字符串为空
        if(StringTools.isEmptyOrNull(content))
        {
            return result;
        }
        
        result = new BASE64Encoder().encode(content.getBytes());
        return result;
    }
    
    /**
     * <BASE64解密>
     * <功能详细描述>
     * @param content 需要解密的内容
     * @return 解密结果
     * 
     */
    public static String decrypt(String content)
    {
        // 加密后字符串
        String result = null;
        
        // 如果输入字符串为空
        if(StringTools.isEmptyOrNull(content))
        {
            return result;
        }
        
        try
        {
            result = new String(new BASE64Decoder().decodeBuffer(content));
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
        }
        return result;
    }
    
}
