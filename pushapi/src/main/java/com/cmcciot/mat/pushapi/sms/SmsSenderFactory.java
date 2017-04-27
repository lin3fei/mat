package com.cmcciot.mat.pushapi.sms;

import com.cmcciot.mat.pushapi.sms.impl.CpsdnaCTSmsSender;
import com.cmcciot.mat.pushapi.sms.util.Util;

public class SmsSenderFactory
{

	public static Class<?> getExtSender(String corpId)
	{
		if (corpId == null || corpId.equals(""))
		{
			return null;
		}
		return Util.senderMap.get(corpId);
	}

	public static Class<?> getSmsSender(String corpId)
	{
		Class<?> clazz = getExtSender(corpId);
		if (clazz == null)
		{
			clazz = CpsdnaCTSmsSender.class;
		}
		return clazz;
	}
}
