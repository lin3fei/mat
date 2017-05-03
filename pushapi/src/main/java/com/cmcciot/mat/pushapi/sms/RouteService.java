package com.cmcciot.mat.pushapi.sms;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.cmcciot.mat.pushapi.cmobile.server.SendMessage;
import com.cmcciot.mat.pushapi.push.PushApiVersion;
import com.cmcciot.mat.pushapi.push.Pusher;
import com.cmcciot.mat.pushapi.push.android.AndroidPusher;
import com.cmcciot.mat.pushapi.push.ios.IosPusher;
import com.cmcciot.mat.pushapi.sms.impl.CpsdnaCTSmsAgent;
import com.cmcciot.mat.pushapi.sms.intf.SmsSender;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sms.ydsms.YDSendMessage;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.xinhi.pub.PubUtils;

/**
 * 业务逻辑分发类
 * 
 * @author catigger
 */
public class RouteService {
	private static final String SENDER_CHECK_TOKEN = "dina.cpsdna.org";
	JsonParser jsonp = new JsonParser();
	JsonElement requestParams = null;
	private static RouteService routeService = null;

	public void parse(String jsonString) {
		requestParams = jsonp.parse(jsonString);
	}

	public String getCommand() {
		if (requestParams.getAsJsonObject().has("cmd")) {
			return requestParams.getAsJsonObject().get("cmd").getAsString()
					.trim();
		} else {
			return null;
		}

	}

	public JsonObject getParams() {
		if (requestParams.getAsJsonObject().has("params")) {
			return requestParams.getAsJsonObject().get("params")
					.getAsJsonObject();
		} else {
			return null;
		}
	}

	public static RouteService getRouteService() {
		if (routeService == null) {
			return new RouteService();
		} else {
			return routeService;
		}
	}

	private RouteService() {

	}

	private boolean isAuthed(String token) {
		if (SENDER_CHECK_TOKEN.equals(token)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public JsonObject route(String jsonString, HttpServletRequest hsrequest) {
		JsonObject jo = new JsonObject();
		try {
			parse(jsonString);
			String reqCmd = getCommand();
			jo.addProperty("cmd", reqCmd);
			JsonObject params = getParams();
			if (params == null) {
				jo.addProperty("result", 1);
				jo.addProperty("resultNote", "request params blank");
				return jo;
			}
			String sendToken = params.has("smsAuthKey") ? Util.getJString(
					params, "smsAuthKey") : Util
					.getJString(params, "sendToken");
			if (!isAuthed(sendToken) && !"activityCheck".equals(reqCmd)) {
				jo.addProperty("result", 1);
				jo.addProperty("resultNote",
						"request params  sendToken invalid.");
				return jo;
			}

			if ("sendSms".equals(reqCmd)) {// 早期短信分发入口，目前AAA在调用

				String receiveNo = params.has("receiveNo") ? params.get(
						"receiveNo").getAsString() : "";
				String msgContent = params.has("msgContent") ? params.get(
						"msgContent").getAsString() : "";
				String busMsgId = params.has("busMsgId") ? params.get(
						"busMsgId").getAsString() : "";
				String userId = params.has("userId") ? params.get("userId")
						.getAsString() : "";
				String corpId = params.has("corpId") ? params.get("corpId")
						.getAsString() : "";
				boolean online = params.has("online") ? params.get("online")
						.getAsBoolean() : false;

				if ("chinamobile".equals(Util.convertToString(Consts.sp)
						.toLowerCase())) {// 中移动短信网关
					boolean sendResult = new SendMessage().sendSMS(receiveNo,
							msgContent, userId, PubUtils.getUniqueSn() + "");
					jo.addProperty("result", sendResult ? 0 : 1);
					jo.addProperty("resultNote", "Send SMS "
							+ (sendResult ? "successed" : "failed"));
					return jo;
				} else if ("yd".equals(Util.convertToString(Consts.sp)
						.toLowerCase())) {// 易达短信网关
					new Thread(new YDSendMessage(receiveNo, msgContent, userId,
							PubUtils.getUniqueSn() + "")).start();
					jo.addProperty("result", 0);
					jo.addProperty("resultNote", "Send SMS successed");
					return jo;
				} else {
					// 获取发送的实例化的类
					Class<?> impl = SmsSenderFactory.getSmsSender(corpId);
					Constructor<?> sender = impl.getConstructor(String.class,
							String.class, String.class, String.class);
					// 发送
					if (online) {// 同步
						SmsSender onlineSender = (SmsSender) sender
								.newInstance(receiveNo, msgContent, busMsgId,
										userId);
						boolean sendResult = onlineSender.sendOnline(receiveNo,
								msgContent, busMsgId, userId);
						jo.addProperty("result", sendResult ? 0 : 1);
						jo.addProperty("resultNote", "Send SMS "
								+ (sendResult ? "successed" : "failed"));
						return jo;
					}
					new Thread((SmsSender) sender.newInstance(receiveNo,
							msgContent, busMsgId, userId)).start();
					jo.addProperty("result", 0);
					jo.addProperty("resultNote", "SMS is being sended...");
					return jo;
				}

			} else if ("pushMsg".equals(reqCmd)) {
				int pushMode = params.has("pushMode") ? params.get("pushMode")
						.getAsInt() : 0;
				String pushId = params.has("pushId") ? params.get("pushId")
						.getAsString().trim() : "";
				String appName = params.has("appName") ? params.get("appName")
						.getAsString().trim() : "";// xfinder4person车行者
													// xfinder4company 车掌柜
				String userId = params.has("userId") ? params.get("userId")
						.getAsString() : "";
				String sp = Util.getJString(params, "sp");
				String mobile = "";
				int online = 0; // 是否同步发送
				JsonObject msgBody = params.has("msgBody") ? params.get(
						"msgBody").getAsJsonObject() : (new JsonObject());
				if (msgBody == null || msgBody.entrySet().size() == 0) {
					jo.addProperty("result", 1);
					jo.addProperty("resultNote", "Lack of element msgBody.");
					return jo;
				}
				/*
				 * 将appName添加到msgBody里，方便IOS在PUSH时判断调用相关p12文件，
				 * 调用成功发送时要将msgBody里的appName属性去除
				 */
				msgBody.addProperty("appName", appName);

				int msgType = 0;
				int taskType = 0;
				int caseOper = 0;
				String objId = null;
				String lpno = null;
				String idName = null;
				String corpId = null;
				String recUid = PubUtils.getUniqueSn() + "";
				String msg = Util.getJString(msgBody, "msg");
				String caseId = Util.getJString(msgBody, "caseId");

				if (pushMode == 0) {// 网络push消息
					msgType = Util.getJInt(msgBody, "msgType", 0);
					taskType = Util.getJInt(msgBody, "taskType", 0);
					objId = Util.getJString(msgBody, "objId");
					idName = Util.getJString(msgBody, "idName");
					lpno = Util.getJString(msgBody, "lpno");
					caseOper = Util.getJInt(msgBody, "caseOper", caseOper);
				} else if (pushMode == 1) {// 发送短信
					mobile = params.has("mobile") ? params.get("mobile")
							.getAsString() : "";
					if (mobile == null || "".equals(mobile)) {
						jo.addProperty("result", 2);
						jo.addProperty("resultNote",
								"Send SMS Failed: mobile is null");
						return jo;
					}
					if (!Util.validateMobilePhone(mobile)) {
						jo.addProperty("result", 3);
						jo.addProperty("resultNote",
								"Send SMS Failed: mobile is invalidate");
						return jo;
					}
					if (Consts.blackList.indexOf(mobile) >= 0) {
						jo.addProperty("result", 4);
						jo.addProperty("resultNote",
								"Send SMS Failed: mobile is put on a blacklist");
						return jo;
					}
					corpId = msgBody.has("corpId") ? msgBody.get("corpId")
							.getAsString() : "";
					online = params.has("online") ? params.get("online")
							.getAsInt() : 0;
					idName = msgBody.has("idName") ? msgBody.get("idName")
							.getAsString() : "";
					lpno = msgBody.has("lpno") ? msgBody.get("lpno")
							.getAsString() : "";
				}

				if (pushMode == 1
						&& ("chinaMobile".equals(sp) || "chinamobile"
								.equals(Util.convertToString(Consts.sp)
										.toLowerCase()))) {// 中移动短信网关
					boolean sendResult = new SendMessage().sendSMS(mobile, msg,
							userId, recUid);
					jo.addProperty("result", sendResult ? 0 : 1);
					jo.addProperty("resultNote", "Send SMS "
							+ (sendResult ? "successed." : "failed"));
					return jo;
				} else if (pushMode == 1
						&& ("yd".equals(sp) || "yd".equals(Util
								.convertToString(Consts.sp).toLowerCase()))) {
					// 易达短信网关
					new Thread(new YDSendMessage(mobile, msg, userId, recUid))
							.start();
					jo.addProperty("result", 0);
					jo.addProperty("resultNote", "Send SMS successed");
					return jo;
				} else if (pushMode == 1) {
					Class<?> impl = SmsSenderFactory.getSmsSender(corpId);
					Constructor<?> sender = impl.getConstructor(String.class,
							String.class, String.class, String.class);
					// 发送
					if (online == 0) {
						SmsSender onlineSender = (SmsSender) sender
								.newInstance(mobile, msg, caseId, userId);
						boolean sendResult = onlineSender.sendOnline(mobile,
								msg, caseId, userId);
						jo.addProperty("result", sendResult ? 0 : 1);
						jo.addProperty("resultNote", "Send SMS "
								+ (sendResult ? "successed." : "failed"));
						return jo;
					}
					new Thread((SmsSender) sender.newInstance(mobile, msg,
							caseId, userId)).start();
					jo.addProperty("result", 0);
					jo.addProperty("resultNote", "SMS is being sended...");
				} else if (pushMode == 0) {
					msgBody.addProperty("pushTime",
							Util.datetime2Str(new Date()));
					msgBody.addProperty("recUid", recUid);
					if (pushId != null && !"".equals(pushId)) {
						msgBody.addProperty("pushId", pushId);
						int plat = 0;
						if (pushId.length() == 71) {
							plat = 1;
						}
						msgBody.addProperty("platform", plat);
						Pusher.push(msgBody);
					} else {
						jo.addProperty("result", 1);
						jo.addProperty("resultNote", "pushId is null.");
						return jo;
					}
				} else {
					jo.addProperty("result", 1);
					jo.addProperty("resultNote",
							"request params pushMode invalid.");
					return jo;
				}

				jo.addProperty("result", 0);
				jo.addProperty("resultNote", "Push message successed.");
				return jo;
			} else if ("rawPush".equals(reqCmd)) {
				String pushId = params.has("pushId") ? params.get("pushId")
						.getAsString() : null;
				String appName = params.has("appName") ? params.get("appName")
						.getAsString() : null;
				JsonObject body = params.has("body") ? params.get("body")
						.getAsJsonObject() : null;
				/*
				 * 将appName添加到msgBody里，方便IOS在PUSH时判断调用相关p12文件，
				 * 调用成功发送时要将msgBody里的appName属性去除
				 */
				body.addProperty("appName", appName);

				int plat = params.has("plat") ? params.get("plat").getAsInt()
						: 1;
				if (pushId != null && !pushId.equals("") && body != null
						&& !body.equals("")) {
					if (plat == 0) { // android 平台
						new Thread(new AndroidPusher(pushId, body.toString()))
								.start();
						Util.log.warn("push to android: [" + pushId + "] : "
								+ body);
					} else if (plat == 1) {// ios 平台
						new Thread(new IosPusher(pushId, body.toString()))
								.start();
						Util.log.warn("push to IOS: [" + pushId + "] : " + body);
					}
				}
				jo.addProperty("result", 0);
				jo.addProperty("resultNote", "raw push commited");
				return jo;
			} else if ("syncPushMsgStatus".equals(reqCmd)) {// 同步消息状态
				Pusher.feedbackMsg(params);
			} else if ("sysRefresh".equals(reqCmd)) {// 刷新系统配置
				// Util.log.setLevel(Level.toLevel(params.get("logLevel").getAsString().toUpperCase()));
				boolean refresh = params.get("refresh").getAsBoolean();
				if (refresh) {
					Util.loadConfig();
					CpsdnaCTSmsAgent.stopSmsServer();
					CpsdnaCTSmsAgent.startSmsServer();
				}

				jo.addProperty("result", 0);
				jo.addProperty("resultNote", "Success");
				JsonObject detail = new JsonObject();

				Set<Entry<Object, Object>> sett = Util.p.entrySet();
				Iterator<Entry<Object, Object>> iterator = sett.iterator();
				while (iterator.hasNext()) {
					Entry<Object, Object> entry = iterator.next();
					String key = String.valueOf(entry.getKey());
					String value = String.valueOf(entry.getValue());
					detail.addProperty(key, value);
				}

				detail.addProperty("smsServerIp", Consts.smsServerIp);
				detail.addProperty("smsServerPort", Consts.smsServerPort);
				detail.addProperty("accessUserName", Consts.accessUserName);
				detail.addProperty("accessPassword", Consts.accessPassword);
				detail.addProperty("blackList", Consts.blackList);
				detail.addProperty("callbackUrl", Consts.callbackUrl);

				jo.add("detail", detail);
				return jo;

			} else if ("query".equals(reqCmd)) {// 查询SQLite表记录
				String tabName = params.has("tabName") ? params.get("tabName")
						.getAsString() : "";
				String day = params.has("day") ? params.get("day")
						.getAsString() : "";
				String field = "";
				if ("SMS_MSG".equals(tabName.toUpperCase())) {
					field = "CREAT_TIME";
				} else if ("PUSH_MSG".equals(tabName.toUpperCase())) {
					field = "PUSH_TIME";
				} else if ("BUS_MESSAGE".equals(tabName.toUpperCase())) {
					field = "MSG_SEND_TIME";
				}
				try {
					String querySQL = "select * from " + tabName
							+ " where  date(" + field + ")='" + day + "'";
					Util.log.debug("querySQL=" + querySQL);
					List<Object[]> list = SQLiteExecute.executeQuery(querySQL);
					JsonArray dataArr = new JsonArray();
					for (Object[] arr : list) {
						JsonArray ja = new JsonArray();
						for (Object obj : arr) {
							ja.add(new JsonPrimitive(obj == null ? "" : obj
									.toString()));
						}
						dataArr.add(ja);
					}
					jo.addProperty("dataCount", list != null ? list.size() : 0);
					jo.add("data", dataArr);

				} catch (Exception e) {
					Util.log.error("",e);
					jo.addProperty("result", 1);
					jo.addProperty("resultNote",
							"query failure:" + e.getMessage());
				}

				jo.addProperty("result", 0);
				jo.addProperty("resultNote", "query success");
				return jo;

			} else if ("activityCheck".equals(reqCmd)) {// 查询版本信息
				PushApiVersion pushApiVersion = new PushApiVersion();
				return pushApiVersion.activityCheck();
			}
		} catch (Exception e) {
			Util.log.error("", e);
		}
		return null;
	}

	public String getRequestContent(HttpServletRequest request,
			String requestMode) {
		String req = null;
		// @extract data from get mode
		if (requestMode.equalsIgnoreCase("get")) {
			String command = request.getParameter("q");
			try {
				String postBody = new String(command.getBytes("ISO-8859-1"),
						"UTF-8");
				req = URLDecoder.decode(postBody, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				Util.log.error("", e1);
			}
		}
		// @extract data from post post
		else if (requestMode.equalsIgnoreCase("post")) {
			byte[] buffer = new byte[request.getContentLength()];
			try {
				request.getInputStream().read(buffer);
				request.getInputStream().close();
			} catch (IOException e1) {
				Util.log.error("", e1);
			}
			try {
				String postBody = new String(buffer, "UTF-8");
				req = URLDecoder.decode(postBody, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				Util.log.error("", e1);
			}
		}
		return req;
	}

	public void doProcess(HttpServletRequest request,
			HttpServletResponse response) {
		String req = null;
		if (request.getQueryString() != null
				&& !request.getQueryString().equals("")) {
			req = getRequestContent(request, "get");
		} else {
			req = getRequestContent(request, "post"); // post方式
		}
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			JsonObject result = route(req, request);
			if (result == null) {
				String cmd = getCommand();
				JsonObject jo = new JsonObject();
				jo.addProperty("cmd", cmd);
				jo.addProperty("result", 1);
				jo.addProperty("resultNote", "request content format invalid.");
				writer.write(jo.toString());
			} else {
				writer.write(result.toString()); // 输出到请求端
			}
		} catch (IOException e2) {
			Util.log.error("", e2);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
}
