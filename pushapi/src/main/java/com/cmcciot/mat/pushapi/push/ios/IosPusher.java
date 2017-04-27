package com.cmcciot.mat.pushapi.push.ios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.commons.lang.StringUtils;

import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IosPusher implements Runnable
{
	//final private static String appleGwUrl = "gateway.sandbox.push.apple.com";
	//	final private static String appleGwUrl = "gateway.push.apple.com";
	//	final private static String appleFbUrl = "feedback.push.apple.com";
	//	final private static int appleGwPort = 2195;
	//	final private static int appleFbPort = 2196;
	final private static String rootOrgPath = IosPusher.class.getResource("/").toString();
	final private static String rootWinPath = rootOrgPath.substring(6); // 删除括号中的内容[file:/]
	final private static String rootLinPath = rootOrgPath.substring(5); // 删除括号中的内容[file:]
	final private static String osName = System.getProperty("os.name");
	final private static String password = "cpsdna";//导出的证书密码，不能为空

	public static HashMap<String, String> p12Map = new HashMap<String, String>();//存放p12文件路径

	public static HashMap<String, PushNotificationManager> pushMngMap = new HashMap<String, PushNotificationManager>();//存放PushManager

	private String pushId = null;
	private String pushMsg = null;

	public IosPusher(String id, String msg)
	{
		pushId = id;
		pushMsg = msg;
	}

	private static String getOsPath()
	{
		if (osName.equalsIgnoreCase("Linux"))
		{
			return rootLinPath;
		}
		return rootWinPath;
	}

	/**
	 * 初始化PushManager
	 * 
	 * @param appName
	 * @return
	 */
	public static PushNotificationManager getInstance(String appName)
	{
		PushNotificationManager pushManager = null;

		try
		{
			pushManager = pushMngMap.get(appName);

			if (pushManager == null && !StringUtils.isBlank(p12Map.get(appName)))
			{
				Util.log.info("init PushNotificationManager start");
				Util.log.info(getOsPath() + "/" + p12Map.get(appName));
				pushManager = new PushNotificationManager();
				pushManager.initializeConnection(new AppleNotificationServerBasicImpl(getOsPath() + "/" + p12Map.get(appName), password, true));
				pushMngMap.put(appName, pushManager);
				Util.log.info("init PushNotificationManager success");
			}
		}
		catch (CommunicationException ce)
		{
			ce.printStackTrace();
			Util.log.error("init PushNotificationManager error:" + ce);
		}
		catch (KeystoreException ke)
		{
			ke.printStackTrace();
			Util.log.error("init PushNotificationManager error:" + ke);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error("init PushNotificationManager error:" + e);
		}

		return pushManager;
	}

	public synchronized static void send(String pushId, String msg)
	{
		Util.log.warn("start push msg[" + msg + "] to ios [" + pushId + "]");
		String appName="";
		try
		{
			JsonParser jsonp = new JsonParser();
			JsonObject msgJson = jsonp.parse(msg).getAsJsonObject();
			/*
			 * 取出appName
			 */
			appName = Util.getJString(msgJson, "appName", Consts.APPNAME_CHEXINGZHE);
			msgJson.remove("appName");

			PushNotificationManager pushManager = getInstance(appName);
			if (pushManager == null)
			{
				Util.log.error("push msg[" + msg + "] to ios [" + pushId + "] failed:  pushManager is null, please check " + appName + " p12 file");
			}

			PushNotificationPayload localPayLoad = new PushNotificationPayload();
			if (msgJson.has("aps"))
			{//新的消息格式
				JsonObject apsJson = msgJson.get("aps").getAsJsonObject();
				String alert = Util.getJString(apsJson, "alert");
				Util.log.debug("alert length [ " + alert.length() + "]  alert content [" + alert + "] ");
				localPayLoad.addAlert(alert);//push的内容
				localPayLoad.addBadge(Util.getJInt(apsJson, "badge", 0));//图标小红圈的数值
				localPayLoad.addSound(StringUtils.isBlank(Util.getJString(apsJson, "sound")) ? "News_Flash.caf" : Util.getJString(apsJson, "sound"));//铃音
				localPayLoad.addCustomDictionary("msgType", Util.getJString(apsJson, "msgType"));//消息类型
			}
			else
			{//旧的消息格式
				localPayLoad.addAlert(Util.getJString(msgJson, "msg"));// push的内容
				localPayLoad.addSound("default");//铃音
				msgJson.remove("msg");
				for (Map.Entry<String, JsonElement> msgBody : msgJson.entrySet())
				{
					localPayLoad.addCustomDictionary(msgBody.getKey(), msgBody.getValue().getAsString());
				}
			}

			//指定PUSH的设备
			Device device = new BasicDevice();
			device.setToken(pushId.replace(" ", ""));

			//PUSH消息
			PushedNotification notification = pushManager.sendNotification(device, localPayLoad, true);
			
			//定义结果返回列表
			List<PushedNotification> notifications = new ArrayList<PushedNotification>();
			notifications.add(notification);
			List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
			List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
			int failed = failedNotifications.size();
			int successful = successfulNotifications.size();

			if (successful > 0)
			{
				Util.log.warn("push msg[" + msg + "] to ios [" + pushId + "] success.");
			}
			else
			{
				Util.log.error("push msg[" + msg + "] to ios [" + pushId + "] failed:" + failed);
			}
		}
		catch (Exception localException)
		{
			pushMngMap.remove(appName);
			localException.printStackTrace();
			Util.log.error("push msg[" + msg + "] to ios [" + pushId + "] failed:" + localException.getMessage());
		}
	}

	@Override
	public void run()
	{
		send(pushId, pushMsg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String puId = "a5f9bea6 9cb5d037 c5cc7dc7 85c450db e81b8373 9292ca7a 428fc815 251b7a0d";
		String msg = "{\"appName\":\"xfinder4personal\",\"aps\": {\"alert\": \"bnx测试\",\"badge\": 1,\"sound\": \"sound.aiff\",\"msgType\": 0}}";
		IosPusher pusher = new IosPusher(puId, msg);
		new Thread(pusher).start();
	}

}
