package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.util.Date;
import java.util.Random;

import com.cmcciot.mat.pushapi.push.URLInvoke;
import com.cmcciot.mat.pushapi.sms.impl.SmsMsg;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;
import com.google.gson.JsonObject;
import com.xinhi.pub.PubUtils;

public class CallBackThread extends Thread
{

	/**
	 * 用户手机号码
	 */
	private String phone;
	/**
	 * 用户上行短信消息内容
	 */
	private String msg;

	private JsonObject callBackJson = new JsonObject();

	private JsonObject callBackParamJson = new JsonObject();

	public CallBackThread(String phone, String msg)
	{
		this.phone = phone;
		this.msg = msg;
	}

	@Override
	public void run()
	{

		try
		{
			/*
			 * 保存消息记录
			 */
			Random rand = new Random();
			SmsMsg sendMsg = new SmsMsg();
			sendMsg.setSmsid(PubUtils.getUniqueSn() + "");
			sendMsg.setCreatTime(new Date());
			sendMsg.setMsgSrcType(2);//上行消息
			sendMsg.setMsgType(1);// 文件消息
			sendMsg.setMsgContent(msg);
			sendMsg.setReceiverNo(phone);
			sendMsg.setReceiverNum(1);// 只发送一个号码
			sendMsg.setMsgSrcKey("chinamobile");
			sendMsg.setUserId("");
			sendMsg.setProcessTime(new Date());
			sendMsg.setProcessStatus(1);//1成功发送 ，0未成功发送
			StringBuffer insertSQL = new StringBuffer("insert into sms_msg(SMSID,RECEIVER_NUM,RECEIVER_NO,MSG_TYPE,MSG_CONTENT,MSG_SRC_TYPE,MSG_SRC_KEY,USER_ID,CREAT_TIME,PRIORITY,PROCESS_STATUS,PROCESS_TIME,PROCESS_ISMGNO,SYS_SMS_ID)values(").append("'").append(sendMsg.getSmsid()+""+rand.nextInt(4)).append("',").append(sendMsg.getReceiverNum()).append(",'").append(sendMsg.getReceiverNo()).append("',").append(sendMsg.getMsgType()).append(",").append("'").append(sendMsg.getMsgContent()).append("',").append(sendMsg.getMsgSrcType()).append(",").append("'").append(sendMsg.getMsgSrcKey()).append("',").append("'").append(sendMsg.getUserId()).append("',").append("datetime('").append(Util.datetime2Str(sendMsg.getCreatTime())).append("'),").append(sendMsg.getPriority()).append(",").append(sendMsg.getProcessStatus()).append(",").append("datetime('").append(Util.datetime2Str(sendMsg.getProcessTime())).append("'),").append("'").append(sendMsg.getProcessIsmgno()).append("',").append("'").append(sendMsg.getSysSmsId()).append("')");
			SQLiteExecute.executeInsert(insertSQL.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error("save receiveMsg failed:" + e);
		}

		// TODO Auto-generated method stub
		callBackJson.addProperty("cmd", "smsCallback");
		callBackJson.add("params", callBackParamJson);
		callBackParamJson.addProperty("phone", phone);
		callBackParamJson.addProperty("msg", msg);

		Util.log.info("REQ>>" + callBackJson.toString() + "  to " + Consts.callbackUrl);
		String result = URLInvoke.post(Consts.callbackUrl, callBackJson.toString());
		Util.log.info("RES>>" + result);
	}
}
