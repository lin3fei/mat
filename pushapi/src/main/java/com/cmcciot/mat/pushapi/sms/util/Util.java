package com.cmcciot.mat.pushapi.sms.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.pushapi.sysrefresh.SysConfigFlush;
import com.google.gson.JsonObject;

public class Util
{

	final public static Logger log = LoggerFactory.getLogger(Util.class);
	final public static Map<String, Class<?>> senderMap = new HashMap<String, Class<?>>();

	public static Properties p = new Properties();

	public synchronized static void loadConfig()
	{
		try
		{
			InputStream in = Util.class.getResourceAsStream("/sysconfig.properties");
			p.load(in);
			SysConfigFlush.refreshURL(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Util.log.error("加载配置文件出错");
		}
	}

	/**
	 * 格式为yyyyMMdd
	 * 
	 * @param date
	 * @return 格式化后的字符串
	 */
	public static String date2Strs(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format(date);
		}
		return null;
	}

	/**
	 * 格式为yyyy-MM-dd
	 * 
	 * @param date
	 * @return 格式化后的字符串
	 */
	public static String date2Str(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		}
		return null;
	}

	/**
	 * 格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return 格式化后的字符串
	 */
	public static String datetime2Str(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		}
		return null;
	}

	/**
	 * 格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return 格式化后的字符串
	 */
	public static Date str2Datetime(String date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				return sdf.parse(date);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 格式为yyyyMMddHHmmss
	 * 
	 * @param date
	 * @return 格式化后的字符串
	 */
	public static String datetime2Strs(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			return sdf.format(date);
		}
		return null;
	}

	/**
	 * 得到当前日期，格式'yyyy-MM-dd'
	 * 
	 * @author bnx
	 * @return
	 */
	public static String getCurrentDate()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(calendar.getTime());
	}

	/**
	 * 得到当前日期，格式'yyyy-MM-dd HH:mm:ss'
	 * 
	 * @author bnx
	 * @return
	 */
	public static String getCurrentDateTime()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(calendar.getTime());
	}

	/**
	 * 将对象转换成不含空格的字符串
	 * 
	 * @author bnx
	 * @param obj
	 * @return
	 */
	public static String convertToString(Object obj)
	{
		if (obj == null)
			return "";
		return obj.toString().trim();
	}

	/**
	 * 以字符串形式返回JsonObject中的值
	 * 
	 * @author bnx
	 * @since 2012-05-24
	 * @param jsonObject
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String getJString(JsonObject jsonObject, String key, String defaultVal)
	{
		String defaultValue = "";
		if (defaultVal != null)
		{
			defaultValue = defaultVal;
		}
		return (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) ? jsonObject.get(key).getAsString().trim() : defaultValue;
	}

	public static String getJString(JsonObject jsonObject, String key)
	{
		return getJString(jsonObject, key, null);
	}

	/**
	 * 以double形式返回JsonObject中的值
	 * 
	 * @author bnx
	 * @since 2012-05-24
	 * @param jsonObject
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static double getJDouble(JsonObject jsonObject, String key, Double defaultVal)
	{
		Double defaultValue = 0.00;
		if (defaultVal != null)
		{
			defaultValue = defaultVal;
		}
		return (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) ? ("".equals(jsonObject.get(key).getAsString().trim()) ? defaultValue : jsonObject.get(key).getAsDouble())
				: defaultValue;
	}

	public static double getJDouble(JsonObject jsonObject, String key)
	{
		return getJDouble(jsonObject, key, null);
	}

	/**
	 * 以int形式返回JsonObject中的值
	 * 
	 * @author bnx
	 * @since 2012-05-24
	 * @param jsonObject
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Integer getJInt(JsonObject jsonObject, String key, Integer defaultVal)
	{
		Integer defaultValue = 0;
		if (defaultVal != null)
		{
			defaultValue = defaultVal;
		}
		return (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) ? ("".equals(jsonObject.get(key).getAsString().trim()) ? defaultValue : jsonObject.get(key).getAsInt())
				: defaultValue;
	}

	public static Integer getJInt(JsonObject jsonObject, String key)
	{
		return getJInt(jsonObject, key, null);
	}

	// 手机号码正则表达式
	private static Pattern phonePattern = Pattern.compile("^([0-9]{11})|([0-9]{13})$");

	/**
	 * 校验手机号码格式.
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean validateMobilePhone(String phone)
	{
		return phone != null && !"".equals(phone.trim()) && phonePattern.matcher(phone).matches();
	}

	public static String getRealIp()
	{
		String netip = null;// 外网IP
		try
		{
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded)
			{
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements())
				{
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
					{// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return netip;
	}

	private static int sequenceId = 0;//序列编号

	public static int getSequence()
	{
		++sequenceId;
		if (sequenceId > 255)
		{
			sequenceId = 0;
		}
		return sequenceId;
	}

	/**
	 * 截取字节
	 * 
	 * @param msg
	 * @param start
	 * @param end
	 * @return
	 */
	public static byte[] getMsgBytes(byte[] msg, int start, int end)
	{
		byte[] msgByte = new byte[end - start];
		int j = 0;
		for (int i = start; i < end; i++)
		{
			msgByte[j] = msg[i];
			j++;
		}
		return msgByte;
	}

	public static byte[] UTF16BEToGBK(byte[] msg)
	{
		try
		{
			String str = new String(msg, "UTF-16BE");
			return str.getBytes("GBK");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error("UTF-16BE to GBK error:" + e);
		}
		return null;
	}

	public static void main(String[] args)
	{
		try
		{
			String singleMsg="[行车卫士提醒您]尊敬的用户，您的设备（胜利所长风社区RT938）于2014-，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html，http://58.215.50.61:19080/saasapi/saasapi.html";
			byte[] msg_content =singleMsg.getBytes("UTF-16BE");
		//	byte[] gbk_msg_content1=Util.UTF16BEToGBK(msg_content);
		//	System.out.println(new String(gbk_msg_content1,"GBK"));
			byte[] msgHead = new byte[6];
			//表示剩余协议头的长度
			msgHead[0] = 0x05;
			msgHead[1] = 0x00;
			msgHead[2] = 0x03;
			//这批短信的唯一标志，事实上，SME(手机或者SP)把消息合并完之后， 就重新记录，所以这个标志是否唯一并不是很重要。 
			msgHead[3] = (byte) Util.getSequence();
			//这批短信的数量。如果一个超长短信总共5条，这里的值就是5。
			msgHead[4] = (byte) 2;
			msgHead[5] = 0x01;
			int maxLength=140;
			int msgLength=msg_content.length;
			int msgSendCount = msgLength % (maxLength - 6) == 0 ? msgLength / (maxLength - 6) : msgLength / (maxLength - 6) + 1;
			for (int i = 0; i < msgSendCount; i++)
			{//逐条发送
			byte[] needMsg = null;
			//消息头+消息内容拆分
			if (i != msgSendCount - 1)
			{
				int start = (maxLength - 6) * i;
				int end = (maxLength - 6) * (i + 1);
				needMsg = Util.getMsgBytes(msg_content, start, end);
			}
			else
			{
				int start = (maxLength - 6) * i;
				int end = msg_content.length;
				needMsg = Util.getMsgBytes(msg_content, start, end);
			}
			//int subLength = needMsg.length + msgHead.length;
			System.out.println("utf16=="+new String(needMsg, "UTF-16BE"));
			byte[] gbk_msg_content=Util.UTF16BEToGBK(needMsg);
			System.out.println("gbk=="+new String(gbk_msg_content,"GBK"));
			byte[] sendMsg = new byte[gbk_msg_content.length + msgHead.length];
			System.arraycopy(msgHead, 0, sendMsg, 0, 6);
			System.arraycopy(gbk_msg_content, 0, sendMsg, 6, gbk_msg_content.length);
			System.out.println("--"+new String(sendMsg,"GBK"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
