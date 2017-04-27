package com.cmcciot.mat.pushapi.sms.impl;

import java.util.Date;
import java.util.List;

import com.cmcciot.mat.pushapi.sms.util.Util;
import com.cmcciot.mat.pushapi.sqlite.SQLiteExecute;
import com.linkage.netmsg.server.AnswerBean;
import com.linkage.netmsg.server.ReceiveMsg;
import com.linkage.netmsg.server.ReturnMsgBean;
import com.linkage.netmsg.server.UpMsgBean;

public class CpsdnaCTSmsHandler extends ReceiveMsg
{

	/* 获取下行短信返回状态和短信ID的方法 */
	public void getAnswer(AnswerBean answerBean)
	{
	//	Session sess = HibernateSessionFactory.getSession();
		super.getAnswer(answerBean);

		/* 序列Id */
		String seqIdString = answerBean.getSeqId();

		/* 短信状态 ,0表示提交至API平台成功 */
		int status = answerBean.getStatus();

		/* 下行短信ID，用来唯一标识一条下行短信 */
		String msgId = answerBean.getMsgId();

		// 此处加入接收短信返回状态和短信ID的处理代码（即:将接收到的信息做入库处理）
		String query1="select  smsid from sms_msg where smsid='"+ Util.date2Strs(new Date()) + seqIdString+"'";
		List<Object[]> orgMsg=SQLiteExecute.executeQuery(query1);
		if (orgMsg != null && orgMsg.size()>0 && status == 0)
		{// 有消息记录，有正确的返回状态才修改状态
			// 设置已回执字段。3已回执，2已发送，1正发送，0未送
			// orgMsg.setSysSmsId(msgId);//系统消息ID，用于回执修改状态使用。
			String update="update Sms_Msg set PROCESS_STATUS=2  where SMSID='"+orgMsg.get(0)[0]+"'";
			SQLiteExecute.executeUpdate(update);
			
//			sess.beginTransaction();
//			sess.saveOrUpdate(orgMsg);
//			sess.getTransaction().commit();
		}
		Util.log.debug("[sendStat]SEQ:" + Util.date2Strs(new Date()) + seqIdString + "\tMsgId:" + msgId + "\tStatus:" + status);
	}

	/* 接收上行短信的方法 */
	public void getUpMsg(UpMsgBean upMsgBean)
	{

		super.getUpMsg(upMsgBean);

		String sequenceId = upMsgBean.getSequenceId();

		/* 发送号码 */
		String sendNum = upMsgBean.getSendNum();

		/* 接收号码 */
		String receiveNum = upMsgBean.getReceiveNum();

		/* 上行短信到达时间 */
		String msgRecTime = upMsgBean.getMsgRecTime();

		/* 短信内容 */
		String msgContent = upMsgBean.getMsgContent();

		// 此处加入接收上行短信的处理代码
		Util.log.debug("[submitSMS]SEQ: " + sequenceId + "\tSendMobileNo: " + sendNum + "\tRecMobileNo: " + receiveNum + "\tGotTime: " + msgRecTime + "\tmsgContent[" + msgContent
				+ "]");

	}

	/* 获取下行短信回执的方法 */
	public void getReturnMsg(ReturnMsgBean returnMsgBean)
	{
		//Session sess = HibernateSessionFactory.getSession();
		super.getReturnMsg(returnMsgBean);

		String sequenceId = returnMsgBean.getSequenceId();

		/* 短信的msgId */
		String msgId = returnMsgBean.getMsgId();

		/* 发送号码 */
		String sendNum = returnMsgBean.getSendNum();

		/* 接收号码 */
		String receiveNum = returnMsgBean.getReceiveNum();

		/* 短信提交时间 */
		String submitTime = returnMsgBean.getSubmitTime();

		/* 短信下发时间 */
		String sendTime = returnMsgBean.getSendTime();

		/* 短信状态 */
		String msgStatus = returnMsgBean.getMsgStatus();

		/* 短信错误代码 */
		int msgErrStatus = returnMsgBean.getMsgErrStatus();

		// 此处加入接收短信回执的处理代码

		// 更新系统消息状态
		//SmsMsg orgMsg = (SmsMsg) sess.createQuery("from SmsMsg where smsid = '" + msgId + "'").uniqueResult();
		String update0="select  Msg_Src_Key  from Sms_Msg  where smsid='"+msgId+"'";
		List<Object[]> orgMsg= SQLiteExecute.executeQuery(update0);
		if (orgMsg != null && orgMsg.size()>0)
		{// 有消息记录，有正确的返回状态才修改状态
			// 设置已回执字段。3已回执，2已发送，1正发送，0未送
			String update1="update Sms_Msg  set Process_Status=3  where  smsid='"+msgId+"'";
			SQLiteExecute.executeUpdate(update1);
			// 更新业务消息状态
			String query0="select BUSMSG_ID from Bus_Message  where  BUSMSG_ID='"+orgMsg.get(0)[0]+"'";
			List<Object[]> bMsg = SQLiteExecute.executeQuery(query0);
			if (bMsg != null && bMsg.size()>0 && "DELIVRD".equals(msgStatus))
			{
				//1 已回执
				String update2="update Bus_Message  set MSG_SEND_RETURN=1 where  BUSMSG_ID='"+bMsg.get(0)[0]+"'";
				SQLiteExecute.executeUpdate(update2);
			}
		}
		Util.log.debug("[return]SEQ: " + sequenceId + " MsgId: " + msgId + " SendMobileNo: " + receiveNum + " RecMobileNo: " + sendNum + " UpTime: " + submitTime + " SendTime: "
				+ sendTime + " MsgStatus: [" + msgStatus + "] ErrorStatus: " + msgErrStatus);
	}

}
