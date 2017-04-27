package com.cmcciot.mat.pushapi.push;

import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.google.gson.JsonObject;

public class PushApiVersion
{
	/**
	 * 软件版本号
	 */

	public static final String version = "IOV_PUSHAPI_1.03.011";

	/**
	 * 版本编译日期
	 */

	public static final String buildTime = "2014-03-03";

	/**
	 * 发布信息说明 格式：1,xxx\n 2,xxx
	 */
	public static final String releaseNote = "支持易达短信网关";

	public JsonObject activityCheck()
	{
		JsonObject repJson = new JsonObject();
		JsonObject resObject = new JsonObject();
		resObject.addProperty("module", "pushapi");
		resObject.addProperty("version", version);
		resObject.addProperty("versionDate", buildTime);
		resObject.addProperty("upTime", Consts.upTime);
		resObject.addProperty("status", "OK");
		resObject.addProperty("jdk", System.getProperty("java.version"));
		resObject.addProperty("maxMemory", Runtime.getRuntime().maxMemory());
		resObject.addProperty("totalMemory", Runtime.getRuntime().totalMemory());
		resObject.addProperty("freeMemory", Runtime.getRuntime().freeMemory());
		resObject.addProperty("threads", Thread.activeCount());
		repJson.addProperty("result", "0");
		repJson.addProperty("resultNote", "success");
		repJson.addProperty("cmd", "activityCheck");
		repJson.add("detail", resObject);
		return repJson;
	}
}
