package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class StringTools
{
    
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * 字符非空验证
     * 
     * @param str
     * @return
     */
    public static boolean isEmptyOrNull(String str)
    {
        if (null == str || "".equals(str.trim()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 返回字节数组的字符串形式
     * 例如：[12,23,127,-44]
     * <功能详细描述>
     * @param data
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getBytesString(byte[] data)
    {
        if (data == null)
        {
            return null;
        }
        if (data.length == 0)
        {
            return "[]";
        }
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < data.length; i++)
        {
            byte b = data[i];
            if (i == 0)
            {
                result.append(b);
                continue;
            }
            result.append("," + b);
        }
        result.append("]");
        return result.toString();
    }
    
    /**
     * 返回字节数组的16进制字符串形式
     * 例如：[0x12,0xff]
     * @param data
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String bytesToHexString(byte[] data)
    {
        if (data == null)
        {
            return null;
        }
        if (data.length == 0)
        {
            return "[]";
        }
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < data.length; i++)
        {
            byte b = data[i];
            if (i == 0)
            {
                result.append(byteToHexString(b));
                continue;
            }
            result.append("," + byteToHexString(b));
        }
        result.append("]");
        return result.toString();
    }
    
    /**
     * 返回字节的16进制字符串形式
     * 例如：0x12
     * @param b
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String byteToHexString(byte b)
    {
        int i = b & 0xff;
        int high = i >>> 4;
        int lower = i & 0x0f;
        
        return "0x" + getChar(high) + getChar(lower);
        
    }
    
    /**
     * 将整数转为16进制字符串
     * <功能详细描述>
     * @param value
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String intToHexString(int value)
    {
        StringBuilder sb = new StringBuilder("0x");
        for (int i = 0; i < 8; i++)
        {
            int temp = (value >> ((7 - i) * 4)) & 0x0f;
            sb.append(getChar(temp));
        }
        return sb.toString();
    }
    
    private static String getChar(int b)
    {
        if (b < 10)
        {
            return b + "";
        }
        if (b == 10)
        {
            return "A";
        }
        if (b == 11)
        {
            return "B";
        }
        if (b == 12)
        {
            return "C";
        }
        if (b == 13)
        {
            return "D";
        }
        if (b == 14)
        {
            return "E";
        }
        if (b == 15)
        {
            return "F";
        }
        return null;
    }
    
    /**
     * 判断对象是否为空
     * <功能详细描述>
     * @param obj
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmptyOrNull(Object obj)
    {
        if (null == obj)
        {
            return true;
        }
        if (obj instanceof String)
        {
            return isEmptyOrNull((String) obj);
        }
        return false;
    }
    
    /**
     * 是否是数字
     * 
     * @param str
     * @return
     */
    public static boolean isNumber(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Boolean boo = false;
        if (null != str && !"".equals(str))
        {
            boo = pattern.matcher(str).matches();
        }
        return boo;
    }
    
    /**
    * 生成与原值不同的随机验证码(纯数字)
    * <功能详细描述>
    * @return [参数说明]
    * 
    * @return String [返回类型说明]
    * @exception throws [违例类型] [违例说明]
    * @see [类、类#方法、类#成员]
    */
    public static String BuildRandomNumber(int number, String oldVerifyCode)
    {
        if (number <= 0)
        {
            return "";
        }
        
        String newVerifyCody = "";
        Random random = new Random();
        for (int i = 0; i < number; i++)
        {
            newVerifyCody += String.valueOf(random.nextInt(10));
        }
        
        if (isEmptyOrNull(oldVerifyCode))
        {
            return newVerifyCody;
        }
        
        while (oldVerifyCode.equals(newVerifyCody))
        {
            newVerifyCody = "";
            for (int i = 0; i != number; i++)
            {
                newVerifyCody += String.valueOf(random.nextInt(10));
            }
        }
        
        return newVerifyCody;
    }
    
    /**
     * 获取字符串长度
     * <一句话功能简述>
     * <功能详细描述>
     * @param str
     * @return [参数说明]
     * 
     * @return int [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static int getLength(String str)
    {
        int length = 0;
        if (StringTools.isEmptyOrNull(str))
        {
            return length;
        }
        str = str.replaceAll("[^\\x00-\\xff]", "**");
        length = str.length();
        return length;
    }
    
    @SuppressWarnings("finally")
    public static String replaceSensInfo(String str)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            map = JacksonUtil.jsonToMap(str);
            String password = (String)map.get("password");
            String phoneNo = (String)map.get("phoneNo");
            String token = (String)map.get("token");
            if(!isEmptyOrNull(password)){
                map.put("password", "******");
            }
            if(!isEmptyOrNull(phoneNo)){
                map.put("phoneNo", "******");
            }
            if(!isEmptyOrNull(token)){
                map.put("token", "******");
            }
            str = JacksonUtil.mapToJson(map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return str;
        }
        
    }
    
    /**
     * 将流转换为字符串
     * <功能详细描述>
     * @param is
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String convertStreamToString(InputStream is)
    {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;
        
        try
        {
            while ((size = is.read(bytes)) > 0)
            {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }
    
    /**
     * 替换短信模板
     * <功能详细描述>
     * @param content
     * @param str
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String replaceContent(String content, String... str)
    {
        if (StringTools.isEmptyOrNull(content))
        {
            return "";
        }
        for (int i = 0; i < str.length; i++)
        {
            content = content.replaceAll("\\$\\{" + i + "\\}", str[i]);
        }
        return content;
    }
    /**
     * 把null字符串替换成指定字符串
     * <功能详细描述>
     * @param str
     * @param replaceStr
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String replaceNullString(String str,String replaceStr){
        
        if(str == null || str.equalsIgnoreCase("null")){
            
            return replaceStr;
            
        }
        
        return str;
        
        
    }
    
    /**
     * 转换给定日期为ISO8601格式
     * <功能详细描述>
     * @param date
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String dateToISO8601(Date date){
        if(date == null || !(date instanceof java.util.Date)){
            
            return "";
            
        }
        return format.format(date);
    }
    
}
