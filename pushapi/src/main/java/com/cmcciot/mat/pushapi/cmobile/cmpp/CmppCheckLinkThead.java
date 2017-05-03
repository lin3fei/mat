package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.cmobile.object.SingleCmppObject;
import com.cmcciot.mat.pushapi.cmobile.server.ParseConfig;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.simpleteam.connection.ConnectionException;

public final class CmppCheckLinkThead extends Thread
{

	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(SocketConstant.LINK_TEST_TIME);
			}
			catch (InterruptedException e)
			{
				Util.log.error(e.getMessage(), e);
			}
			//	Util.log.info("CMPP CHECK LINK BEGIN !!!");
			Set<Entry<String, ServerObject>> entrySet = ParseConfig.getCityInfoByAreaCode().entrySet();
			Iterator<Entry<String, ServerObject>> iterator = entrySet.iterator();
			String key = null;
			while (iterator.hasNext())
			{
				Entry<String, ServerObject> tmpEntry = iterator.next();
				key = tmpEntry.getKey();
				if (!ParseConfig.getCmppSendHashMap().get(key).isAlive())
				{
					Util.log.debug("CmppCheckLinkThead  "+key+"   is  FALSE");
					initServer(key);
				}
			}
			//		Util.log.info("CMPP CHECK LINK END !!!");
		}
	}

	private void initServer(String key)
	{
		Util.log.error(key + " is closed !!!!  CmppCheckLinkThead");
		ServerObject serverObject = ParseConfig.getCmppSendHashMap().get(key).getServerObject();

		try
		{
			CmppSendThread cmppSendThread = ParseConfig.getCmppSendHashMap().get(key);
			SingleCmppObject singleCmppObject = cmppSendThread.getSingleCmppObject();
			if (singleCmppObject.getConnection()!=null)
			{
				// 关闭链接
				try
				{
					singleCmppObject.getConnection().close();
				}
				catch (ConnectionException e)
				{
					Util.log.error(e.getMessage(), e);
				}
				// Log event.
				Util.log.info("CmppCheckLinkThead connection was closed !");
			}
			else
			{
				Util.log.info("CmppCheckLinkThead connection has already been closed !");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error(" CmppCheckLinkThead close " + key + " failed:" + e.getMessage());
		}

		Util.log.info("CmppCheckLinkThead  init serverObject.getAreaCode()=" + (serverObject != null ? serverObject.getAreaCode() : ""));
		// 初始化客户端
		CmppInit smgpInit = new CmppInit(serverObject);
		if (smgpInit.init(serverObject))
		{
			// 启动发送线程
			CmppSendThread smgpSendThread = new CmppSendThread(serverObject, smgpInit.getSingleCmppObject());
			smgpSendThread.setName(serverObject.getAreaCode() + "_CMPP_SEND_THREAD");
			smgpSendThread.start();
			ParseConfig.getCmppSendHashMap().put(serverObject.getAreaCode(), smgpSendThread);
			// Start Receive Thread
			CmppReceiveThread smgpReceiveThread = new CmppReceiveThread(serverObject, smgpInit.getSingleCmppObject());
			smgpReceiveThread.setName(serverObject.getAreaCode() + "_CMPP_RECEIVE_THREAD");
			smgpReceiveThread.start();
			ParseConfig.getCmppReceiveHashMap().put(serverObject.getAreaCode(), smgpReceiveThread);
			// 启动链路检测线程
			CmppActiveTestThread smgpCheckLinkThread = new CmppActiveTestThread(serverObject);
			smgpCheckLinkThread.setName(serverObject.getAreaCode() + "_CMPP_CHECK_THREAD");
			smgpCheckLinkThread.start();
			ParseConfig.getCmppCheckHashMap().put(serverObject.getAreaCode(), smgpCheckLinkThread);
			Util.log.info("CmppCheckLinkThead Init Smgp Server Successfully!!!");
			// 存储配置信息
			ParseConfig.getCityInfoByAreaCode().put(serverObject.getAreaCode(), serverObject);
		}
		else
		{
			Util.log.error(" CmppCheckLinkThead Init Smgp Server Error!!!");
		}

	}

}
