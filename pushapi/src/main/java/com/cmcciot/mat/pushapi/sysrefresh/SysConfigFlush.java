package com.cmcciot.mat.pushapi.sysrefresh;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.cmcciot.mat.pushapi.push.ios.IosPusher;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;

/**
 * 系统配置类
 * 
 * @author catigger
 */
public class SysConfigFlush
{

	public SysConfigFlush()
	{
	}

	public static synchronized void refreshURL(boolean refresh)
	{
		Util.log.info("loading urls begin");
		if (refresh)
		{
			Consts.smsServerIp = Util.p.getProperty("smsServerIp");
			Consts.smsServerPort = Integer.parseInt(Util.p.getProperty("smsServerPort") == null ? "9005" : Util.p.getProperty("smsServerPort"));
			Consts.accessUserName = Util.p.getProperty("accessUserName");
			Consts.accessPassword = Util.p.getProperty("accessPassword");
			Consts.blackList = Util.p.getProperty("blackList");
			Consts.sp = Util.p.getProperty("sp");
			Consts.callbackUrl = Util.p.getProperty("callbackUrl");
			
			Consts.ydurl=Util.p.getProperty("ydurl");
			Consts.sname=Util.p.getProperty("sname");
			Consts.spwd=Util.p.getProperty("spwd");
			Consts.scorpid=Util.p.getProperty("scorpid");

			Set<Entry<Object, Object>> set = Util.p.entrySet();
			Iterator<Entry<Object, Object>> iterator = set.iterator();
			while (iterator.hasNext())
			{
				Entry<Object, Object> entry = iterator.next();
				String key = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				IosPusher.p12Map.put(key, value);
			}

		}
		Util.log.info("loading urls finished");
	}
}
