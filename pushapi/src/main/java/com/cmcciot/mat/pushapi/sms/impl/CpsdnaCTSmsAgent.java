package com.cmcciot.mat.pushapi.sms.impl;

import java.util.Date;
import java.util.List;

import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.linkage.netmsg.NetMsgclient;
import com.linkage.netmsg.server.ReceiveMsg;

public class CpsdnaCTSmsAgent
{
	// 电信企信通配置
	//	private  int smsServerPort = 9005;
	//	private String smsServerIp = "202.102.41.101";
	/*
	 * 开发使用
	 */
	//	private String accessUserName = "025C00173783";
	//	private String accessPassword = "2wsxzaq13e";

	/*
	 * 运营使用
	 */
	//	private static final String accessUserName = "025C00297896";
	//	private static final String accessPassword = "123456ab";

	private static NetMsgclient client = null;
	private static boolean isLogin = false;

	static
	{
		startSmsServer();
	}

	public static void startSmsServer()
	{
		if (client == null)
		{
			client = new NetMsgclient();
		}

		// 消息接收实体bean
		ReceiveMsg msgBean = new CpsdnaCTSmsHandler();

		/* 链接初始化 */
		Util.log.debug("SMS server authorizing ...");
		client.initParameters(Consts.smsServerIp, Consts.smsServerPort, Consts.accessUserName, Consts.accessPassword, msgBean);
		Util.log.debug("SMS server authorize finished.");
		try
		{
			/* 认证 */
			isLogin = client.anthenMsg(client);
			if (isLogin)
			{
				Util.log.debug("=========================================");
				Util.log.debug("==========SMS Thread start success=======");
				Util.log.debug("=========================================");
			}
			else
			{
				Util.log.debug("-----------------------------------------");
				Util.log.debug("-------login denied! password error.-----");
				Util.log.debug("-----------------------------------------");
			}
		}
		catch (Exception e)
		{
			Util.log.debug("Access sms server failed.");
		}
	}

	public static boolean isRunning()
	{
		if (client == null)
		{
			return false;
		}
		if (!client.isStoped)
		{
			return true;
		}
		return false;
	}

	public static void stopSmsServer()
	{
		if (client != null)
		{
			boolean stat = false;
			try
			{
				client.forceDisconnect();
				client.closeConn();
				stat = client.isStoped;
			}
			catch (Exception e)
			{
				Util.log.error("-----------------------------------------");
				Util.log.error("----Disconnect SMS connection failed.----");
				Util.log.error("-----------------------------------------");
			}
			finally
			{
				Util.log.debug("=========================================");
				Util.log.debug("=======SMS Thread stop " + stat + "======");
				Util.log.debug("=========================================");
				client = null;
			}
		}
	}

	public boolean sendMsg(NetMsgclient client, List<String> mobileNos, String msg)
	{
		if (client != null && !client.isStoped)
		{
			try
			{
				for (String mNo : mobileNos)
				{
					if (isValidNo(mNo))
					{
						String ret = client.sendMsg(client, 1, mNo, msg, 1);
						// 成功的返回 是9位字符串（HHmmssSSS）
						if (ret != null && ret.length() == 9)
						{
							return true;
						}
					}
				}
			}
			catch (Exception e)
			{
				Util.log.error("Sending msg to [" + mobileNos + "] content is [" + msg + "]failed.");
			}
		}
		return false;
	}

	public boolean sendMsg(List<String> mobileNos, String msg)
	{
		if (client != null && !client.isStoped)
		{
			try
			{
				for (String mNo : mobileNos)
				{
					if (isValidNo(mNo))
					{
						String ret = client.sendMsg(client, 1, mNo, msg, 1);
						// 成功的返回 是9位字符串（HHmmssSSS）
						if (ret != null && ret.length() == 9)
						{
							return true;
						}
					}
				}
			}
			catch (Exception e)
			{
				Util.log.error("Sending msg to [" + mobileNos + "] content is [" + msg + "]failed.");
			}
		}
		return false;
	}

	/**
	 * 消息记录及发送模块
	 * 
	 * @param mobileNos
	 *            接收信息的号码
	 * @param msg
	 *            接收的消息内容
	 * @return 是否发送成功
	 */
	public boolean sendMsg(String mobileNos, String msg)
	{
		try
		{
			if (client != null && !client.isStoped)
			{
				Date sendTime = new Date();
				String ret = client.sendMsg(client, 1, mobileNos, msg, 1);
				SmsMsg sendMsg = new SmsMsg();
				Date processTime = new Date();
				sendMsg.setSmsid(Util.date2Strs(processTime) + ret);// 消息id为日期加序列号
				sendMsg.setCreatTime(sendTime);
				sendMsg.setMsgSrcType(1);// 非报警消息
				sendMsg.setMsgType(1);// 文件消息
				sendMsg.setMsgContent(msg);
				sendMsg.setReceiverNo(mobileNos);
				sendMsg.setReceiverNum(1);// 只发送一个号码
				// 成功的返回 是9位字符串（HHmmssSSS）
				if (ret != null && ret.length() == 9)
				{
					sendMsg.setProcessTime(processTime);
					sendMsg.setProcessStatus(1);// 正在发送
					new Thread(new CpsdnaCTSmsSaver(sendMsg)).start();
					return true;
				}
				else
				{
					sendMsg.setProcessTime(processTime);
					sendMsg.setProcessStatus(0);// 未发送成功
					new Thread(new CpsdnaCTSmsSaver(sendMsg)).start();
					return false;
				}
			}
		}
		catch (Exception e)
		{
			Util.log.error("Sending msg to [" + mobileNos + "] content is [" + msg + "]failed.");
		}
		return false;
	}

	/**
	 * 带消息来源的短信发送模块
	 * 
	 * @param mobNo
	 *            短信接收号码
	 * @param msg
	 *            短信消息
	 * @param busMsgId
	 *            业务消息标识
	 * @param userId
	 *            发送消息账户标识
	 * @return
	 */
	public boolean sendMsg(String mobNo, String msg, String busMsgId, String userId)
	{

		try
		{
			if (client != null && !client.isStoped)
			{
				Date sendTime = new Date();
				String ret = null;
				try
				{
					//Util.log.info("SEND TO SMS SUCCESS");
					ret = client.sendMsg(client, 1, mobNo, msg, 1);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Util.log.error("send sms to [" + mobNo + "] failed.");
					
					if (!Consts.smsExceptionStatus)
					{//出现异常情况再重新启动一下短信网关
						Consts.smsExceptionStatus = true;
						Util.log.info("start smsServer begin");
						stopSmsServer();
						startSmsServer();
						Util.log.info("start smsServer end");
						Consts.smsExceptionStatus = false;
					}
				}
				SmsMsg sendMsg = new SmsMsg();
				Date processTime = new Date();
				sendMsg.setSmsid(Util.date2Strs(processTime) + ret);// 消息id为日期加序列号
				sendMsg.setCreatTime(sendTime);
				sendMsg.setMsgSrcType(1);// 非报警消息
				sendMsg.setMsgType(1);// 文件消息
				sendMsg.setMsgContent(msg);
				sendMsg.setReceiverNo(mobNo);
				sendMsg.setReceiverNum(1);// 只发送一个号码
				sendMsg.setMsgSrcKey(busMsgId);
				sendMsg.setUserId(userId);
				Util.log.debug("send sms to [" + mobNo + "] ret=" + ret);
				// 成功的返回 是9位字符串（HHmmssSSS）
				if (ret != null && ret.length() == 9)
				{
					sendMsg.setProcessTime(processTime);
					sendMsg.setProcessStatus(1);// 正在发送
					new Thread(new CpsdnaCTSmsSaver(sendMsg)).start();
					return true;
				}
				else
				{
					sendMsg.setProcessTime(processTime);
					sendMsg.setProcessStatus(0);// 未发送成功
					new Thread(new CpsdnaCTSmsSaver(sendMsg)).start();
					return false;
				}
			}
			else
			{
				Util.log.error("send sms to [" + mobNo + "] failed: client is stoped");
			}
		}
		catch (Exception e)
		{
			Util.log.error("send sms to [" + mobNo + "] content is [" + msg + "]failed.");
		}
		return false;
	}

	/**
	 * 验证发送号码
	 * 
	 * @param mNo
	 *            需要验证的号码
	 * @return 是否合法
	 */
	private static boolean isValidNo(String mNo)
	{
		if (mNo == null || "".equals(mNo.trim()))
		{
			return false;
		}
		if (mNo.length() == 11 && mNo.matches("\\d*") && mNo.startsWith("1"))
		{
			return true;
		}
		if (mNo.length() == 14 && mNo.matches("\\+\\d*") && mNo.startsWith("+861"))
		{
			return true;
		}
		return false;
	}

}
