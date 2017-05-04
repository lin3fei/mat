package com.cmcciot.mat.elecalarm.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Tools
{
    
    public static String generateSHA1Str(String str)
    {
        MessageDigest md = null;
        String outStr = null;
        try
        {
            md = MessageDigest.getInstance("SHA-1"); //选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(str.getBytes()); //返回的是byet[]，要转化为String存储比较方便
            outStr = bytetoString(digest);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            nsae.printStackTrace();
        }
        return outStr;
    }
    
    public static String bytetoString(byte[] digest)
    {
        
        String tempStr = "";
        StringBuffer strb = new StringBuffer();
        
        for (int i = 1; i < digest.length; i++)
        {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1)
            {
                strb.append('0').append(tempStr);
            }
            else
            {
                strb.append(tempStr);
            }
        }
        return strb.toString().toLowerCase();
    }
    
}
