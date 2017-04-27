package com.cmcciot.mat.pushapi.sms.impl;

import com.cmcciot.mat.pushapi.sms.intf.SmsSender;
import com.cmcciot.mat.pushapi.sms.util.Util;
import com.linkage.netmsg.NetMsgclient;
import com.linkage.netmsg.server.ReceiveMsg;

public class CpsdnaCTSmsSender implements SmsSender
{
	private String cellNo;
	private String msgContent;
	private String busMsgId;
	private String userId;

	public CpsdnaCTSmsSender()
	{
	}

	public CpsdnaCTSmsSender(String cellNo, String msgContent)
	{
		this.cellNo = cellNo;
		this.msgContent = msgContent;
		this.busMsgId = null;
		this.userId = null;
	}

	public CpsdnaCTSmsSender(String cellNo, String msgContent, String busMsgId, String userId)
	{
		this.cellNo = cellNo;
		this.msgContent = msgContent;
		this.busMsgId = busMsgId;
		this.userId = userId;
	}

	@Override
	public boolean send(String cellNo, String msgContent)
	{
		Util.log.debug("Sending sms to [" + cellNo + "] async mode ...");
		new Thread(new CpsdnaCTSmsSender(cellNo, msgContent)).start();
		return true;
	}

	public boolean send(String cellNo, String msgContent, String busMsgId, String userId)
	{
		Util.log.debug("Sending sms to [" + cellNo + "] by [" + userId + "] async mode ...");
		new Thread(new CpsdnaCTSmsSender(cellNo, msgContent, busMsgId, userId)).start();
		return true;
	}

	@Override
	public boolean sendOnline(String cellNo, String content)
	{
		Util.log.debug("Sending sms to [" + cellNo + "] sync mode ...");
		return new CpsdnaCTSmsAgent().sendMsg(cellNo, content);
	}

	public boolean sendOnline(String cellNo, String msgContent, String busMsgId, String userId)
	{
		Util.log.debug("Sending sms to [" + cellNo + "] by [" + userId + "] sync mode ...");
		return new CpsdnaCTSmsAgent().sendMsg(cellNo, msgContent, busMsgId, userId);
	}

	@Override
	public void run()
	{
		sendOnline(cellNo, msgContent, busMsgId, userId);
	}
	
	
	/**
     * 连接测试
     */

    public static void main(String[] args) {
        NetMsgclient client   = new NetMsgclient();
        
        /*ReceiveMsgImpl为ReceiveMsg类的子类，构造时，构造自己继承的子类就行*/
     		ReceiveMsg msgBean = new CpsdnaCTSmsHandler();
        
        /*初始化参数*/
        client = client.initParameters("202.102.41.101", 9005, "025C00297896", "123456ab",msgBean);
        
        try {
            
            /*登录认证*/
            boolean isLogin = client.anthenMsg(client);
            if(isLogin)System.out.println("login sucess");
            
	            /*发送下行短信*/
	          	client.sendMsg(client, 0, "15950551215", "11111111bnx", 1);
            
           //关闭发送短信与接收短信连接
           client.closeConn();
            
            
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
