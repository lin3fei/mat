package com.cmcciot.mat.pushapi.sms.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.hibernate.Session;

import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;

public class CpsdnaCTSmsSaver implements Runnable
{
	public String getMsgId()
	{
		return msgId;
	}

	public void setMsgId(String msgId)
	{
		this.msgId = msgId;
	}

	public String getSendNo()
	{
		return sendNo;
	}

	public void setSendNo(String sendNo)
	{
		this.sendNo = sendNo;
	}

	public String getRecNo()
	{
		return recNo;
	}

	public void setRecNo(String recNo)
	{
		this.recNo = recNo;
	}

	public String getMsgContent()
	{
		return msgContent;
	}

	public void setMsgContent(String msgContent)
	{
		this.msgContent = msgContent;
	}

	public String getSubmitTime()
	{
		return submitTime;
	}

	public void setSubmitTime(String submitTime)
	{
		this.submitTime = submitTime;
	}

	public String getProcessTime()
	{
		return processTime;
	}

	public void setProcessTime(String processTime)
	{
		this.processTime = processTime;
	}

	int processStatus;

	public int getProcessStatus()
	{
		return processStatus;
	}

	public void setProcessStatus(int processStatus)
	{
		this.processStatus = processStatus;
	}

	public String getMsgStatus()
	{
		return msgStatus;
	}

	public void setMsgStatus(String msgStatus)
	{
		this.msgStatus = msgStatus;
	}

	public int getMsgType()
	{
		return msgType;
	}

	public void setMsgType(int msgType)
	{
		this.msgType = msgType;
	}

	String msgId;
	String sendNo;
	String recNo;
	String msgContent;
	String submitTime;
	String processTime;
	String msgStatus;
	int msgType;

	Session sess;
	SmsMsg msg = null;

	public CpsdnaCTSmsSaver()
	{

	}

	public CpsdnaCTSmsSaver(String msgId, String sendNo, String recNo, String msgContent, String submitTime, String processTime, String msgStatus, int msgType, Session sess)
	{
		this.msgId = msgId;
		this.sendNo = sendNo;
		this.recNo = recNo;
		this.msgContent = msgContent;
		this.submitTime = submitTime;
		this.processTime = processTime;
		this.msgStatus = msgStatus;
		this.msgType = msgType;
		this.sess = sess;
	}

	public CpsdnaCTSmsSaver(SmsMsg msg)
	{
		this.msg = msg;
	}

	@Override
	public void run()
	{
		//sess = HibernateSessionFactory.getSession();
		// 回复消息格式
		// 20110826193530972617 SendMobileNo: 18913925127 RecMobileNo:
		// 02583223602 GotTime: 11/08/26 19:35:30 msgContent[��]
//		if (msgId == null || "".equals(msgId.trim()))
//		{
//			return;
//		}
		Random rand = new Random();
		
		if (msg != null)
		{
			List<Object[]> recordList = SQLiteExecute.executeQuery("select SMSID from sms_msg where SMSID='" + msg.getSmsid() + "'");
			if (recordList.size() > 0)
			{
				StringBuffer updateSQL = new StringBuffer("update sms_msg set ")
				.append("RECEIVER_NUM=").append(msg.getReceiverNum()).append(",")
				.append("RECEIVER_NO='").append(msg.getReceiverNo()).append("',")
				.append("MSG_TYPE=").append(msg.getMsgType()).append(",")
				.append("MSG_CONTENT=").append("'").append(msg.getMsgContent()).append("',")
				.append("MSG_SRC_TYPE=").append(msg.getMsgSrcType()).append(",")
				.append("MSG_SRC_KEY=").append("'").append(msg.getMsgSrcKey()).append("',")
				.append("USER_ID=").append("'").append(msg.getUserId()).append("',")
				.append("CREAT_TIME=").append("datetime('").append(Util.datetime2Str(msg.getCreatTime())).append("'),")
				.append("PRIORITY=").append(msg.getPriority()).append(",")
				.append("PROCESS_STATUS=").append(msg.getProcessStatus()).append(",")
				.append("PROCESS_TIME=").append("datetime('").append(Util.datetime2Str(msg.getProcessTime())).append("'),")
				.append("PROCESS_ISMGNO=").append("'").append(msg.getProcessIsmgno()).append("',")
				.append("SYS_SMS_ID=").append("'").append(msg.getSysSmsId()).append("'")
				.append("  where SMSID='").append(msg.getSmsid()).append("'");
				SQLiteExecute.executeUpdate(updateSQL.toString());
			}
			else
			{
				StringBuffer insertSQL = new StringBuffer("insert into sms_msg(SMSID,RECEIVER_NUM,RECEIVER_NO,MSG_TYPE,MSG_CONTENT,MSG_SRC_TYPE,MSG_SRC_KEY,USER_ID,CREAT_TIME,PRIORITY,PROCESS_STATUS,PROCESS_TIME,PROCESS_ISMGNO,SYS_SMS_ID)values(").append("'").append(msg.getSmsid()+rand.nextInt(4)).append("',").append(msg.getReceiverNum()).append(",'").append(msg.getReceiverNo()).append("',").append(msg.getMsgType()).append(",").append("'").append(msg.getMsgContent()).append("',").append(msg.getMsgSrcType()).append(",").append("'").append(msg.getMsgSrcKey()).append("',").append("'").append(msg.getUserId()).append("',").append("datetime('").append(Util.datetime2Str(msg.getCreatTime())).append("'),").append(msg.getPriority()).append(",").append(msg.getProcessStatus()).append(",").append("datetime('").append(Util.datetime2Str(msg.getProcessTime())).append("'),").append("'").append(msg.getProcessIsmgno()).append("',").append("'").append(msg.getSysSmsId()).append("')");
				SQLiteExecute.executeInsert(insertSQL.toString());
			}
			//sess.beginTransaction();
			//sess.saveOrUpdate(msg);
			//sess.getTransaction().commit();

		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
			SmsMsg sms = new SmsMsg();
			sms.setSmsid(msgId);
			sms.setMsgContent(msgContent);
			sms.setMsgType(1);// 文本消息
			sms.setReceiverNum(1);// 只发送一个号码
			sms.setReceiverNo(recNo);
			sms.setMsgType(msgType);
			try
			{
				if (submitTime != null && !"".equals(submitTime.trim()))
				{
					sms.setCreatTime(sdf.parse(submitTime));
				}
				else
				{
					sms.setCreatTime(new Date());
				}
			}
			catch (ParseException e1)
			{
				sms.setCreatTime(new Date());
				// e1.printStackTrace();
				System.out.println("Parsing submitTime failed, use current time.");
			}
			try
			{
				if (processTime != null && !"".equals(processTime.trim()))
				{
					sms.setProcessTime(sdf.parse(processTime));
				}
				else
				{
					sms.setProcessTime(new Date());
				}
			}
			catch (ParseException e)
			{
				// e.printStackTrace();
				System.out.println("Parsing processTime failed, use current time.");
				sms.setProcessTime(new Date());
			}
			// sms.setSendNo(sendNo);
			// sms.setSubmitTime(sdf.parse(submitTime));
			if ("DELIVRD".equals(msgStatus))
			{
				sms = new SmsMsg();
				sms.setSmsid(msgId);
				sms.setProcessStatus(1);
			}
			try
			{
				sms.setProcessStatus(processStatus);
				
				List<Object[]> recordList = SQLiteExecute.executeQuery("select SMSID from sms_msg where SMSID='" + msg.getSmsid() + "'");
				if (recordList.size() > 0)
				{
					StringBuffer updateSQL = new StringBuffer("update sms_msg set ")
					.append("RECEIVER_NUM=").append(msg.getReceiverNum()).append(",")
					.append("RECEIVER_NO='").append(msg.getReceiverNo()).append("',")
					.append("MSG_TYPE=").append(msg.getMsgType()).append(",")
					.append("MSG_CONTENT=").append("'").append(msg.getMsgContent()).append("',")
					.append("MSG_SRC_TYPE=").append(msg.getMsgSrcType()).append(",")
					.append("MSG_SRC_KEY=").append("'").append(msg.getMsgSrcKey()).append("',")
					.append("USER_ID=").append("'").append(msg.getUserId()).append("',")
					.append("CREAT_TIME=").append("datetime('").append(Util.datetime2Str(msg.getCreatTime())).append("'),")
					.append("PRIORITY=").append(msg.getPriority()).append(",")
					.append("PROCESS_STATUS=").append(msg.getProcessStatus()).append(",")
					.append("PROCESS_TIME=").append("datetime('").append(Util.datetime2Str(msg.getProcessTime())).append("'),")
					.append("PROCESS_ISMGNO=").append("'").append(msg.getProcessIsmgno()).append("',")
					.append("SYS_SMS_ID=").append("'").append(msg.getSysSmsId()).append("'")
					.append("  where SMSID='").append(msg.getSmsid()).append("'");
					SQLiteExecute.executeUpdate(updateSQL.toString());
				}
				else
				{
					StringBuffer insertSQL = new StringBuffer("insert into sms_msg(SMSID,RECEIVER_NUM,RECEIVER_NO,MSG_TYPE,MSG_CONTENT,MSG_SRC_TYPE,MSG_SRC_KEY,USER_ID,CREAT_TIME,PRIORITY,PROCESS_STATUS,PROCESS_TIME,PROCESS_ISMGNO,SYS_SMS_ID)values(").append("'").append(msg.getSmsid()+rand.nextInt(4)).append("',").append(msg.getReceiverNum()).append(",").append("'").append(msg.getReceiverNo()).append("',").append(msg.getMsgType()).append(",").append("'").append(msg.getMsgContent()).append("',").append(msg.getMsgSrcType()).append(",").append("'").append(msg.getMsgSrcKey()).append("',").append("'").append(msg.getUserId()).append("',").append("datetime('").append(Util.datetime2Str(msg.getCreatTime())).append("'),").append(msg.getPriority()).append(",").append(msg.getProcessStatus()).append(",").append("datetime('").append(Util.datetime2Str(msg.getProcessTime())).append("'),").append("'").append(msg.getProcessIsmgno()).append("',").append("'").append(msg.getSysSmsId()).append("')");
					SQLiteExecute.executeInsert(insertSQL.toString());
				}
				
//				sess.beginTransaction();
//				sess.saveOrUpdate(sms);
//				sess.getTransaction().commit();
			}
			catch (Exception e)
			{
				Util.log.error("Save sms to db failed.");
			}
		}

	}
}
