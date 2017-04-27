package com.cmcciot.mat.pushapi.sms;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;

public class TimerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
		Consts.upTime=Util.getCurrentDateTime();
		/*
		 * 加载config文件
		 */
		Util.loadConfig();
		Util.log.info("加载配置文件结束");

		//暂时先注释
//		Timer time = new Timer();
//		time.schedule(new SendMsgTask(), 30000, 5000);
		
	}
}
