package com.cmcciot.mat.pushapi.sms.ydsms;

import java.util.Date;
import java.util.Random;

import com.cmcciot.mat.pushapi.push.URLInvoke;
import com.cmcciot.mat.pushapi.sms.impl.SmsMsg;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;

public class YDSendMessage implements Runnable
{

	String phone;
	String msg;
	String userId;
	String recUid;

	public YDSendMessage(String phone_, String msg_,String userId_,String recUid_)
	{
		phone = phone_;
		msg = msg_;
		userId=userId_;
		recUid=recUid_;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		StringBuffer postBuffer = new StringBuffer("sname=").append(Consts.sname).append("&spwd=").append(Consts.spwd).append("&scorpid=").append(Consts.scorpid).append("&sphones=").append(phone).append("&smsg=").append(msg);
		Util.log.info("start send msg "+postBuffer.toString()+" to "+Consts.ydurl);
		String result=URLInvoke.postWithParams(Consts.ydurl,postBuffer.toString());
		Util.log.info("end send msg result:"+result);
		
		
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
		sendMsg.setReceiverNo(phone);
		sendMsg.setReceiverNum(1);// 只发送一个号码
		sendMsg.setMsgSrcKey("sp");
		sendMsg.setUserId(userId);
		sendMsg.setProcessTime(new Date());
		sendMsg.setProcessStatus(1);//1成功发送 ，0未成功发送
		StringBuffer insertSQL = new StringBuffer("insert into sms_msg(SMSID,RECEIVER_NUM,RECEIVER_NO,MSG_TYPE,MSG_CONTENT,MSG_SRC_TYPE,MSG_SRC_KEY,USER_ID,CREAT_TIME,PRIORITY,PROCESS_STATUS,PROCESS_TIME,PROCESS_ISMGNO,SYS_SMS_ID)values(").append("'").append(sendMsg.getSmsid()+rand.nextInt(4)).append("',").append(sendMsg.getReceiverNum()).append(",'").append(sendMsg.getReceiverNo()).append("',").append(sendMsg.getMsgType()).append(",").append("'").append(sendMsg.getMsgContent()).append("',").append(sendMsg.getMsgSrcType()).append(",").append("'").append(sendMsg.getMsgSrcKey()).append("',").append("'").append(sendMsg.getUserId()).append("',").append("datetime('").append(Util.datetime2Str(sendMsg.getCreatTime())).append("'),").append(sendMsg.getPriority()).append(",").append(sendMsg.getProcessStatus()).append(",").append("datetime('").append(Util.datetime2Str(sendMsg.getProcessTime())).append("'),").append("'").append(sendMsg.getProcessIsmgno()).append("',").append("'").append(sendMsg.getSysSmsId()).append("')");
		SQLiteExecute.executeInsert(insertSQL.toString());
		
	}

}
