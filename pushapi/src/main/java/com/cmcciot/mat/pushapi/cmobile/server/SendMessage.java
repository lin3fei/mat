package com.cmcciot.mat.pushapi.cmobile.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.cmcciot.mat.pushapi.cmobile.object.CmppSendSmsObject;
import com.cmcciot.mat.pushapi.sms.impl.SmsMsg;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;

public class SendMessage
{

	public boolean sendSMS(String mobile, String msg, String userId, String recUid)
	{
		try
		{
			recUid = createSequenceNo();

			CmppSendSmsObject message = new CmppSendSmsObject();
			message.setContent(msg);
			// 整型数据，短信发送序列号，运营商要求在一段时间内必须唯一自增，
			message.setSequenceNo(recUid);
			// 消息接收者
			message.setToUserPhone(mobile);

			if (mobile.matches(Consts.mobilePhoneReg))
			{
				Util.log.debug("走移动网关");
				message.setCarrierType(Consts.carrier_type_chinamobile);
				//发送移动号码
				// 调用发送线程发送短信，“NJ”和配置文件cwebconfig.xml里面的内容对应
				if (ParseConfig.getCmppSendHashMap().get("NJ") != null)
				{
					ParseConfig.getCmppSendHashMap().get("NJ").addMsgToList(message);
				}
			}
			else
			{
				Util.log.debug("走联通和电信网关");
				message.setCarrierType(Consts.carrier_type_unionAndTelecom);
				//发送联通和电信的号码
				if (ParseConfig.getCmppSendHashMap().get("NJ1") != null)
				{
					ParseConfig.getCmppSendHashMap().get("NJ1").addMsgToList(message);
				}
			}

			/*
			 * 保存消息记录
			 */
			Random rand = new Random();
			SmsMsg sendMsg = new SmsMsg();
			sendMsg.setSmsid(recUid);// 消息id为日期加序列号
			sendMsg.setCreatTime(new Date());
			sendMsg.setMsgSrcType(1);// 下行消息
			sendMsg.setMsgType(1);// 文件消息
			sendMsg.setMsgContent(msg);
			sendMsg.setReceiverNo(mobile);
			sendMsg.setReceiverNum(1);// 只发送一个号码
			sendMsg.setMsgSrcKey("chinamobile");
			sendMsg.setUserId(userId);
			sendMsg.setProcessTime(new Date());
			sendMsg.setProcessStatus(1);//1成功发送 ，0未成功发送
			StringBuffer insertSQL = new StringBuffer("insert into sms_msg(SMSID,RECEIVER_NUM,RECEIVER_NO,MSG_TYPE,MSG_CONTENT,MSG_SRC_TYPE,MSG_SRC_KEY,USER_ID,CREAT_TIME,PRIORITY,PROCESS_STATUS,PROCESS_TIME,PROCESS_ISMGNO,SYS_SMS_ID)values(").append("'").append(sendMsg.getSmsid()+""+rand.nextInt(4)).append("',").append(sendMsg.getReceiverNum()).append(",'").append(sendMsg.getReceiverNo()).append("',").append(sendMsg.getMsgType()).append(",").append("'").append(sendMsg.getMsgContent()).append("',").append(sendMsg.getMsgSrcType()).append(",").append("'").append(sendMsg.getMsgSrcKey()).append("',").append("'").append(sendMsg.getUserId()).append("',").append("datetime('").append(Util.datetime2Str(sendMsg.getCreatTime())).append("'),").append(sendMsg.getPriority()).append(",").append(sendMsg.getProcessStatus()).append(",").append("datetime('").append(Util.datetime2Str(sendMsg.getProcessTime())).append("'),").append("'").append(sendMsg.getProcessIsmgno()).append("',").append("'").append(sendMsg.getSysSmsId()).append("')");
			SQLiteExecute.executeInsert(insertSQL.toString());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error("send msg [" + msg + "]  to [" + mobile + "]");
			return false;
		}
		return true;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssms");
		System.out.println(sdf.format(new Date()));

		CmppSendSmsObject message = new CmppSendSmsObject();
		message.setContent("你好!!!!");
		// 整型数据，短信发送序列号，运营商要求在一段时间内必须唯一自增，
		message.setSequenceNo("123456713");
		// 消息接收者
		message.setToUserPhone("13888888888");
		// 调用发送线程发送短信，“NJ”和配置文件cwebconfig.xml里面的内容对应
		if (ParseConfig.getCmppSendHashMap().get("NJ") != null)
		{
			ParseConfig.getCmppSendHashMap().get("NJ").addMsgToList(message);
		}
	}

	//循环产生发送消息的msg_id int
	private String createSequenceNo()
	{
		Consts.count++;
		if(Consts.count>=2000000000){
			Consts.count=0;
		}
		return String.valueOf(Consts.count);
	}

}
