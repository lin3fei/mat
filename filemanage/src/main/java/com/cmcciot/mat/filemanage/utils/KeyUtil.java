/*
 * 文 件 名:  NonceUtil.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月9日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.filemanage.utils;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author Administrator
 * @version [版本号, 2014年4月9日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class KeyUtil
{
    public static void main(String[] args)
    {
//        System.out.println(generateOpaque("1A740E80025F8991936340E5E2A62BB5"));
        System.out.println(getIP());
    }
    
    public static String generateNonce()
    {
        Random rand = new Random();
        String time = System.currentTimeMillis() + "";
        String source = time + "-" + rand.nextInt(10000);
        
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        
        return new String(Hex.encode(digest.digest(source.getBytes())));
    }
    
    /**
     * 生成会话标识
     * @param str
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String generateOpaque(String str)
    {
        if (StringUtil.isEmpty(str))
        {
            str = System.currentTimeMillis() + ";"
                    + new Random().nextInt(20040410);
        }
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(str.getBytes())));
    }
    
    /**
     * MD5加密
     * <功能详细描述>
     * @param str
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String makeMD5(String str)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            String returnStr = new BigInteger(1, md.digest()).toString(16);
            return returnStr;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return str;
    }
    
    /**
     * 时间比较
     * <功能详细描述>
     * @param str
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @throws ParseException 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("static-access")
    public static int compare_date(String life, String stamptime)
            throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        Date startDate = format.parse(stamptime);
        // 请求的时间与有效期的相加计算
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(calendar.SECOND, Integer.parseInt(life));
        Date resultDate = calendar.getTime();
        Date curentDate = new Date();
        
        try
        {
            if (resultDate.getTime() > curentDate.getTime())
            {
                // date1在date2前
                return 1;
            }
            else if (resultDate.getTime() < curentDate.getTime())
            {
                // date1在date2后,过期
                return -1;
            }
            else
            {
                // date1与date2相等
                return 0;
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 获取本地IP（Linux）
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getIP()
    {
        String ip = "";
        try
        {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0"))
                {
                    continue;
                }
                else
                {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements())
                    {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return ip;
    }
}
