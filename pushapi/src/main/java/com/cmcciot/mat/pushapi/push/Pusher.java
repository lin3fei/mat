package com.cmcciot.mat.pushapi.push;

import com.cmcciot.mat.pushapi.push.android.AndroidPusher;
import com.cmcciot.mat.pushapi.push.ios.IosPusher;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Pusher
{
	/**
	 * 将传入的消息，调用相应平台的发送接口，并保存到消息队列中
	 * 
	 * @param msg
	 */
	public static void push(JsonObject msg)
	{
		// PushMsgQueue.msg.add(msg); //重复发送先禁用掉，一次发送
		send(msg);
	}

	public static void send(JsonObject msg)
	{
		int platform = msg.has("platform") ? msg.get("platform").getAsInt() : 0;
		String pushId = msg.has("pushId") ? msg.get("pushId").getAsString() : null;
		if (pushId == null || "".equals(pushId))
		{
			Util.log.error("pushId is blank");
			return;
		}

		// 发送时，将无用信息删除
		JsonElement platformJson = msg.remove("platform");
		JsonElement pushIdJson = msg.remove("pushId");
		JsonElement pushTimeJson = msg.remove("pushTime");
		if (platform == 0)
		{ // android 平台
			new Thread(new AndroidPusher(pushId, msg.toString())).start();
			Util.log.warn("push to android: [" + pushId + "] : " + msg);
		}
		else if (platform == 1)
		{// ios 平台
			new Thread(new IosPusher(pushId, msg.toString())).start();
			Util.log.warn("push to IOS: [" + pushId + "] : " + msg);
		}
		// 发送后再保养起来，作为后面再发送或是移除时比较的条件
		msg.addProperty("platform", platformJson.getAsString());
		msg.addProperty("pushId", pushIdJson.getAsString());
		msg.addProperty("pushTime", pushTimeJson.getAsString());
	}

	public static void dropMsg(JsonObject msg)
	{
		PushMsgQueue.msg.remove(msg);
		Util.log.warn("remove timeout msg [" + msg.get("recUid") + "] :" + msg);
	}

	public static void feedbackMsg(JsonObject params)
	{
		String recUic = params.has("recUid") ? params.get("recUid").getAsString() : "";
		JsonObject defaultMsg = new JsonObject();
		defaultMsg.addProperty("Ack-status", "Ok");
		JsonObject removeMsg = defaultMsg;
		for (JsonObject msg : PushMsgQueue.msg)
		{
			if (recUic.equals(msg.get("recUid").getAsString()))
			{
				removeMsg = msg;
				break;
			}
		}
		PushMsgQueue.msg.remove(removeMsg);
		Util.log.warn("syncPushMsgStatus [" + recUic + "] :" + removeMsg);
	}
}
