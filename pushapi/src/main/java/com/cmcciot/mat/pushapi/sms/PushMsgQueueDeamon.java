package com.cmcciot.mat.pushapi.sms;

import java.util.TimerTask;

import com.cmcciot.mat.pushapi.push.PushMsgQueue;

public class PushMsgQueueDeamon extends TimerTask
{

	@Override
	public void run()
	{
		PushMsgQueue.cleanMsgs();
		//		PushMsgQueue.realTimeSend();
	}

}
