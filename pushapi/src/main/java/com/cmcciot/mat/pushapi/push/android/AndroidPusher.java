package com.cmcciot.mat.pushapi.push.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.cmcciot.mat.pushapi.push.URLInvoke;
import com.cmcciot.mat.pushapi.sms.util.Util;

public class AndroidPusher implements Runnable
{
	final private static String secret = "b90b6d386dcfade";
	final private static String apikey = "3bcce91077f1237";
	private String pushId = null;
	private String msg = null;

	public AndroidPusher(String pushId, String msg)
	{
		this.pushId = pushId;
		this.msg = msg;
	}

	public static void push(String pushId, String msg)
	{
		AndroidPusher pusher = new AndroidPusher(pushId, msg);
		new Thread(pusher).start();
	}

	final private static String pushUrl = "http://www.android-push.com/api/send/?secret=" + secret + "&app_key=" + apikey + "&client_ids=";

	public static void send(String pushId, String msg)
	{
		try
		{
			String encodeMsg = URLEncoder.encode(msg, "utf-8");
			String sendUrl = pushUrl + pushId + "&msg=" + encodeMsg;
			String response = URLInvoke.get(sendUrl);
			Util.log.debug("full request url:     [" + sendUrl + "]");
			Util.log.debug("encoded msg:          [" + encodeMsg + "]");
			Util.log.debug("www.android-push.com: [" + response + "]");
		}
		catch (UnsupportedEncodingException e)
		{
			Util.log.error("invoke [" + msg + "] failed.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.out.println("send " + System.currentTimeMillis());
		String pushId = "94276bcd6bd90530";
		String msg = "{\"msgType\":0,\"taskType\":0,\"objId\":\"\",\"lpno\":\"\",\"idName\":\"\",\"msg\":\"车辆苏A12345还差198公里到保养里程\",\"caseId\":\"234234234\",\"caseOper\":0,\"pushTime\":\"2012-04-25 20:19:14\",\"recUid\":\"wangwb12042520185590006\"}";
		// String msg =
		// "{'msgType':0,'taskType':0,'objId':'','lpno':'','idName':'','msg':'车辆苏A12345还差198公里到保养里程','caseId':'234234234','caseOper':0,'pushTime':'2012-04-25 20:19:14','recUid':'wangwb12042520185590006'}";
		try
		{
			String encodeMsg = URLEncoder.encode(msg, "utf-8");
			Util.log.debug("encode url: " + encodeMsg);
			System.out.println("encode url: " + encodeMsg);
			push(pushId, encodeMsg);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		System.out.println("over " + System.currentTimeMillis());
	}

	@Override
	public void run()
	{
		send(pushId, msg);
	}
}
