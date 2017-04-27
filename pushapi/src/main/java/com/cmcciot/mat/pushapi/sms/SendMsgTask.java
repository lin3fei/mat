package com.cmcciot.mat.pushapi.sms;

import java.util.TimerTask;

import com.cmcciot.mat.pushapi.cmobile.server.SendMessage;
import com.cmcciot.mat.pushapi.sms.util.Util;

public class SendMsgTask extends TimerTask
{

	/**
	 * 消息发送测试
	 */
	public void run()
	{
		Util.log.info("发送消息");
		new SendMessage().sendSMS("13851681926","您好，您的苏A99945的车辆点火启动，请您及时关注详细情况并处理！","1111","");
	}
}
