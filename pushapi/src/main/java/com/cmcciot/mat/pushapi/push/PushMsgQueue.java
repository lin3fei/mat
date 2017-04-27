package com.cmcciot.mat.pushapi.push;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cmcciot.mat.pushapi.sms.util.Util;
import com.google.gson.JsonObject;

public class PushMsgQueue
{
	/**
	 * 消息发送队列
	 */
	final public static ConcurrentLinkedQueue<JsonObject> msg = new ConcurrentLinkedQueue<JsonObject>();

	public static void cleanMsgs()
	{
		Date now = new Date();
		Util.log.warn("push message queue size: " + msg.size());
		for (JsonObject oneMsg : msg)
		{
			Date pushTime = Util.str2Datetime(oneMsg.get("pushTime").getAsString());
			Date rePushTime = new Date(pushTime.getTime() + 1000 * 60 * 5); // 五分钟重发
			Date clearTime = new Date(pushTime.getTime() + 1000 * 60 * 120); // 两小时失效，删除
			if (now.after(rePushTime) && now.before(clearTime))
			{
				Pusher.send(oneMsg);
			}
			else if (now.after(clearTime))
			{
				Pusher.dropMsg(oneMsg);
			}
		}
	}

	// 暂时不用
	public static void realTimeSend()
	{
		while (true)
		{
			for (JsonObject msg : PushMsgQueue.msg)
			{
				Pusher.send(msg);
			}
			try
			{
				Thread.sleep(10L);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
