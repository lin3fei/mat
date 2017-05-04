/*
 * 文 件 名:  ImgFontByte.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年7月18日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.common.util;

import java.awt.Font;
import java.io.ByteArrayInputStream;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Administrator
 * @version  [版本号, 2014年7月18日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ImgFontByte
{
    public Font getFont(int fontHeight)
    {
        try
        {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
                    new ByteArrayInputStream(hex2byte(getFontByteStr())));
            return baseFont.deriveFont(Font.PLAIN, fontHeight);
        }
        catch (Exception e)
        {
            return new Font("Arial", Font.PLAIN, fontHeight);
        }
    }
    
    private byte[] hex2byte(String str)
    {
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        
        byte[] b = new byte[len / 2];
        try
        {
            for (int i = 0; i < str.length(); i += 2)
            {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2))
                        .intValue();
            }
            return b;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /** 
    * ttf字体文件的十六进制字符串 
    * @return 
    */
    private String getFontByteStr()
    {
        return null;
        // return str;//字符串太长 在附件中找  
    }
}
