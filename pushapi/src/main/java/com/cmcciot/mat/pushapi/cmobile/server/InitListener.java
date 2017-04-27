package com.cmcciot.mat.pushapi.cmobile.server;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.sms.util.Consts;
import com.cmcciot.mat.pushapi.sms.util.Util;

/**
 * 系统启动监听器
 * 
 * @author PTERO
 * 
 */
public final class InitListener implements ServletContextListener
{

	/**
	 * 日志Log4j
	 */

	/** 定时器 */
	private static Timer myTimer = new Timer("My_Timer", true);

	/**
	 * 监控平台
	 */
	private static Thread monitorShellThread;

	/**
	 * @return the tmpTimer
	 */
	public static synchronized Timer getMyTimer()
	{
		return myTimer;
	}

	/**
	 * @return the monitorShellThread
	 */
	public static synchronized Thread getMonitorShellThread()
	{
		return monitorShellThread;
	}

	/**
	 * 销毁操作
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{

	}

	/**
	 * 初始化操作
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		Random randow = new Random();
		Consts.SequenceNoRandomIndex = randow.nextInt(100);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		String currentDay = sdf.format(calendar.getTime());
		String sequenceNo =currentDay+ String.valueOf(Consts.SequenceNoRandomIndex);
		Consts.count=Integer.parseInt(sequenceNo);

		// 初始化日志系统
		//initLog4j(servletContextEvent.getServletContext());
		// 读取CWEB配置文件
		initCwebConfig(servletContextEvent.getServletContext());
		Util.log.info("CMPP Started!!!");
	}

	/**
	 * 初始化日志系统
	 * 
	 * @param arg0
	 *            ServletContextEvent
	 */
	//	private void initLog4j(ServletContext servletContext)
	//	{
	//		// 初始化Log4j
	//		String filePath = getCfgFileRealPath(servletContext) + "log4j.xml";
	//		DOMConfigurator.configure(filePath);
	//	}

	/**
	 * 初始话CWEB配置文件
	 * 
	 * @param arg0
	 *            ServletContextEvent
	 */
	private void initCwebConfig(ServletContext servletContext)
	{
		// 读取配置文件CwebConfig.XML
		String configFilePath = getCfgFileRealPath(servletContext);
		SocketConstant.CONFIG_FILE_PATH = configFilePath;
		ParseConfig.parseCwebConfigXmlFile(configFilePath);
	}

	/**
	 * 功能描述：获取WEB-INF目录的绝对路径
	 * 
	 * ServletContext context
	 */
	private String getCfgFileRealPath(ServletContext context)
	{
		// 获取配置文件的绝对路径
		String cfgFilePath = context.getRealPath("/");
		if ((cfgFilePath != null) && !(cfgFilePath.endsWith(File.separator)))
		{
			cfgFilePath += File.separator;
		}
		if (cfgFilePath == null)
		{
			cfgFilePath = "/";
		}
		// 配置文件必须放在WEB－INF目录下
		cfgFilePath += "WEB-INF" + File.separator + "classes" + File.separator;
		return cfgFilePath;
	}
}
