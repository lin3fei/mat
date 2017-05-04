package com.cmcciot.mat.elecalarm.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTools
{
    
    /**
     * 日期格式化
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern)
    {
        String returnValue = "";
        if (date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
    
    /**
     * 日期格式化
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(String date, String pattern) throws Exception
    {
        String returnValue = "";
        if (date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(Long.parseLong(date));
        }
        return (returnValue);
    }
    
    /**
     * 日期格式化
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern,Locale locale)
    {
        String returnValue = "";
        if (date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat(pattern,locale);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
    
    /**
     * 将字符串类型的日期转化成Date类型
     * 
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date parse(String strDate, String pattern)
    {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = null;
        try
        {
            date = df.parse(strDate);
            return date;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将字符串类型的日期转化成UTC时间格式
     * 精确到grade等级，如grade=1精确到毫秒，grade=1000精确到秒
     * @param strDate
     * @param pattern
     * @param grade
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String parseTime(String strDate, String pattern,long grade)
    {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = null;
        try
        {
            date = df.parse(strDate);
            long time = date.getTime() / grade;
            return time+"";
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取时间戳
     */
    public static String getTimeString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }
    
    /**
     * 获取日期年份
     * 
     * @param date
     *            日期
     * @return
     */
    public static String getYear(Date date)
    {
        return format(date, "yyyy-MM-dd HH:mm:ss").substring(0, 4);
    }
    
    /**
     * 按默认格式的字符串距离今天的天数
     * 
     * @param date
     *            日期字符串
     * @return
     */
    public static int countDays(String date)
    {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date, "yyyy-MM-dd HH:mm:ss"));
        long t1 = c.getTime().getTime();
        return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
    }
    
    /**
     * 按用户格式字符串距离今天的天数
     * 
     * @param date
     *            日期字符串
     * @param format
     *            日期格式
     * @return
     */
    public static int countDays(String date, String format)
    {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date, format));
        long t1 = c.getTime().getTime();
        return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
    }
    
    /**
     * 获取某天凌晨时间
     * <一句话功能简述>
     * <功能详细描述>
     * @param date
     * @return [参数说明]
     * 
     * @return Date [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static Date getMorning(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }
    
    /**
     * 获取当前时间的时间戳，精确到毫秒
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getCurrentTimeMillis()
    {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
    
    /**
     * 获取多少天之后的日期
     * <功能详细描述>
     * @param startDate
     * @param days
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getAfterDate(String startDate, int days)
    {
        Date date;
        Calendar cal = Calendar.getInstance();
        try
        {
            date = (new SimpleDateFormat("yyyy-MM-dd")).parse(startDate);
            
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
    }
    
    /**
     *  日期比较
     * <功能详细描述>
     * @param startDate
     * @param endDate
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean compareDate(String startDate, String endDate,
            String format)
    {
        Date newDate = strDateToDate(startDate, format);
        Date newDate2 = strDateToDate(endDate, format);
        return compareDate(newDate, newDate2);
    }
    
    /**
     * 将字符转换为日期类型
     * <功能详细描述>
     * @param strDate
     * @param sourceFormat
     * @return [参数说明]
     * 
     * @return Date [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static Date strDateToDate(String strDate, String sourceFormat)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(sourceFormat);
        Date date = null;
        try
        {
            date = dateFormat.parse(strDate);
            
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    
    /**
     * 判断时间大小
     * <功能详细描述>
     * @param date1
     * @param date2
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean compareDate(Date date1, Date date2)
    {
        if (date1.compareTo(date2) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 获取当前时间
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String now()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
    
    /**
     * 获取几分钟后
     * <功能详细描述>
     * @param date
     * @param minute
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String afterMinute(String date, int minute)
    {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MINUTE, minute);
        Date tasktime = cale.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(tasktime);
    }

    public static Date getAfterMonth(Date date, int months)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        
        return calendar.getTime();
    }
    
    public static Date getAfterDay(Date date, int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        
        return calendar.getTime();
    }
    
    /**
     * 获取当前星期几,0为星期日
     * <功能详细描述>
     * 
     * @return int [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
    
    /**
     * 传入两个时间（格式为时分 10：11），判断当前时间是否在此时间区间之内。没在区间内返回True
     * <功能详细描述>
     * @return boolean [参数说明]
     * 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean compareHM(String startTime, String endTime) {
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	String time = sdf.format(date);
    	if (time.compareTo(startTime) >= 0 && time.compareTo(endTime) < 0) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    /**
     * 传入两个时间（格式为时分 10：11），判断传入时间是否在此当日时间区间之内。没在区间内返回True
     * <功能详细描述>
     * @return boolean [参数说明]
     * 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean compareHMD(String startTime, String endTime, Date date) {
    	Date nowDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    	String time = sdf.format(date);
    	if (time.compareTo(startTime) >= 0 && time.compareTo(endTime) < 0) {
    		if (sdf1.format(nowDate).equals(sdf1.format(date))) {
    			return false;
    		} else {
    			return true;
    		}
    	} else {
    		return true;
    	}
    }
}
