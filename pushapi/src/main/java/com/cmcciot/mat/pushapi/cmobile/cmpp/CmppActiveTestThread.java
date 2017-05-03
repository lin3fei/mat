package com.cmcciot.mat.pushapi.cmobile.cmpp;

import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.CmppSendSmsObject;
import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.cmobile.server.ParseConfig;
import com.cmcciot.mat.pushapi.sms.util.Util;

public class CmppActiveTestThread extends Thread {

	/**
	 * 线程活动标志位
	 */
	private boolean aliveFlag = false;

	/**
	 * 配置信息
	 */
	private ServerObject serverObject;

	/**
	 * @return aliveFlag
	 */
	public synchronized boolean isAliveFlag() {
		return aliveFlag;
	}

	/**
	 * @param aliveFlag
	 *            aliveFlag
	 */
	public synchronized void setAliveFlag(boolean aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	/**
	 * Constructor
	 * 
	 * @param smgpConfig
	 */
	public CmppActiveTestThread(ServerObject serverObject) {
		this.serverObject = serverObject;
	}

	@Override
	public void run() {
		// 设置线程标志位
		setAliveFlag(SocketConstant.TRUE_FLAG);
		while (isAliveFlag()) {
			try {
				
				Thread.sleep(serverObject.getActiveTestTime());
			} catch (InterruptedException e1) {
				setAliveFlag(SocketConstant.FALSE_FLAG);
				Util.log.error(e1.getMessage(), e1);
				Util.log.error("CmppActiveTestThread  "+serverObject.getAreaCode()+"  ActiveTest  sleep falied ");
				// 关闭所有线程
				CmppInit.closeAllThread(serverObject.getAreaCode());
				return;
			}
			// 发送测试，保持socket连接正常
			CmppSendSmsObject message = new CmppSendSmsObject();
			message.setContent("ActiveTest");
			message.setSequenceNo("8888");
			message.setToUserPhone("8888");
			Util.log.debug("ParseConfig.getCmppSendHashMap().get(serverObject.getAreaCode())="+ParseConfig.getCmppSendHashMap().get(serverObject.getAreaCode()));
			if (ParseConfig.getCmppSendHashMap().get(serverObject.getAreaCode()) != null) {
				ParseConfig.getCmppSendHashMap().get(serverObject.getAreaCode()).addMsgToList(message);
				Util.log.debug("CmppActiveTestThread  "+serverObject.getAreaCode()+"  after  sleep "+serverObject.getActiveTestTime()+"ms  ActiveTest   start  send ");
			}
			//	Util.log.info("Send SMGP Check Packet Successfully!!!");
		}
	}
}
