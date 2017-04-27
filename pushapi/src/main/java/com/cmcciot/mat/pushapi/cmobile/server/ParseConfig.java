package com.cmcciot.mat.pushapi.cmobile.server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cmcciot.mat.pushapi.cmobile.cmpp.CmppActiveTestThread;
import com.cmcciot.mat.pushapi.cmobile.cmpp.CmppCheckLinkThead;
import com.cmcciot.mat.pushapi.cmobile.cmpp.CmppInit;
import com.cmcciot.mat.pushapi.cmobile.cmpp.CmppReceiveThread;
import com.cmcciot.mat.pushapi.cmobile.cmpp.CmppSendThread;
import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.ServerObject;
import com.cmcciot.mat.pushapi.sms.util.Util;

/**
 * 配置文件解析类
 * 
 * @author PTERO
 * 
 */
public final class ParseConfig {

	/**
	 * 日志Log4j
	 */

	/**
	 * @return the smgpCheckHashMap
	 */
	public static synchronized ConcurrentHashMap<String, CmppActiveTestThread> getCmppCheckHashMap() {
		return cmppCheckHashMap;
	}

	/**
	 * @return the smgpReceiveHashMap
	 */
	public static synchronized ConcurrentHashMap<String, CmppReceiveThread> getCmppReceiveHashMap() {
		return cmppReceiveHashMap;
	}

	/**
	 * @return the smgpSendHashMap
	 */
	public static synchronized ConcurrentHashMap<String, CmppSendThread> getCmppSendHashMap() {
		return cmppSendHashMap;
	}

	/**
	 * @return the cityInfo
	 */
	public static synchronized ConcurrentHashMap<String, ServerObject> getCityInfoByAreaCode() {
		return cityInfoByAreaCode;
	}

	/**
	 * 解析主配置文件
	 * 
	 * @param configFile
	 *            配置文件
	 */
	@SuppressWarnings("unchecked")
	public static void parseCwebConfigXmlFile(String configFilePath) {
		String cwebConfigFilePath = configFilePath + "CwebConfig.xml";
		File cwebFile = new File(cwebConfigFilePath);
		Util.log.info("Read CwebConfig.XML File Begin!");
		// 解析CwebConfig.XML文件
		try {
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(cwebFile);
			Element rootElement = doc.getRootElement();
			// 读取各通讯模块地址
			SocketConstant.LINK_TEST_TIME = Integer.valueOf(rootElement
					.getChildText("LINK_TEST_TIME"));
			Element cityInfoElemet = rootElement.getChild("CITY_INFO");
			List<Element> cityInfoList = cityInfoElemet.getChildren();
			ServerObject serverObject = null;
			for (Element tmpElement : cityInfoList) {
				serverObject = new ServerObject();
				String areaCode = tmpElement.getAttributeValue("name");
				String protocolType = tmpElement.getAttributeValue("protocol");
				String accessNo = tmpElement.getAttributeValue("accessNo");
				serverObject.setAreaCode(areaCode);
				serverObject.setProtocol(protocolType);
				serverObject.setAccessNo(accessNo);
				
				String cmppServerIp = tmpElement
						.getChildTextTrim("CMMP_SERVER_IP");
				serverObject.setCmppServerIp(cmppServerIp);
				
				//得到本机IP
				String localIp=Util.getRealIp();
				
				Util.log.info("CMMP_SERVER_IP is "+cmppServerIp+", localIp is "+localIp);
				// CMPP协议
				if (protocolType.equals(SocketConstant.CMPP_PROTOCOL)) {

					String account = tmpElement.getChildTextTrim("ACCOUNT");
					serverObject.setAccount(account);
					String password = tmpElement.getChildTextTrim("PASSWORD");
					serverObject.setPassword(password);
					String enterPriseNo = tmpElement
							.getChildTextTrim("ENTERPRISE_NO");
					serverObject.setEnterPriseNo(enterPriseNo);
					String reportNeedFlag = tmpElement
							.getChildTextTrim("REPORT_NEED");
					
					String serviceId=tmpElement
							.getChildTextTrim("SERVICE_ID");
					if(!StringUtils.isEmpty(serviceId)){
						serverObject.setServiceId(serviceId);
					}
					
					if (reportNeedFlag.equals("true")) {
						serverObject.setReportNeedFlag(true);
					} else {
						serverObject.setReportNeedFlag(false);
					}
					int smgpServerPort = Integer.valueOf(tmpElement
							.getChildTextTrim("CMMP_SERVER_PORT"));
					serverObject.setCmppServerPort(smgpServerPort);
					int activeTestTime = Integer.valueOf(tmpElement
							.getChildTextTrim("ACTIVE_TEST_TIME"));
					serverObject.setActiveTestTime(activeTestTime);
					int receiveThreadWaitTime = Integer.valueOf(tmpElement
							.getChildTextTrim("RECEIVE_THREAD_WAIT_TIME"));
					serverObject
							.setReceiveThreadWaitTime(receiveThreadWaitTime);
					
					Util.log.info("serverObject="+serverObject.toString());
					
					
					// 初始化客户端
					CmppInit smgpInit = new CmppInit(serverObject);
					boolean stat = smgpInit.init(serverObject);
					if (stat) {
						Util.log.info("Init CMPP Server Successfully!!!");
					} else {
						Util.log.info("Init CMPP Server Error!!!");
					}
					
					
					// 启动发送线程
					CmppSendThread cmppSendThread = new CmppSendThread(
							serverObject, smgpInit.getSingleCmppObject());
					cmppSendThread.setName(serverObject.getAreaCode()
							+ "_CMPP_SEND_THREAD");
					cmppSendThread.start();
					cmppSendHashMap.put(serverObject.getAreaCode(),
							cmppSendThread);
					// Start Receive Thread
					CmppReceiveThread cmppReceiveThread = new CmppReceiveThread(
							serverObject, smgpInit.getSingleCmppObject());
					cmppReceiveThread.setName(serverObject.getAreaCode()
							+ "_CMPP_RECEIVE_THREAD");
					cmppReceiveThread.start();
					cmppReceiveHashMap.put(serverObject.getAreaCode(),
							cmppReceiveThread);
					// 启动链路检测线程，保持socket连接
					CmppActiveTestThread cmppCheckLinkThread = new CmppActiveTestThread(
							serverObject);
					cmppCheckLinkThread.setName(serverObject.getAreaCode()
							+ "_CMPP_CHECK_THREAD");
					cmppCheckLinkThread.start();
					cmppCheckHashMap.put(serverObject.getAreaCode(),
							cmppCheckLinkThread);
					Util.log.info("Init CMPP Server Successfully!!!");
					// 存储配置信息
					cityInfoByAreaCode.put(areaCode, serverObject);
				}
			}
			// 启动链路检测线程
			CmppCheckLinkThead cmppCheckLinkThead = new CmppCheckLinkThead();
			cmppCheckLinkThead.start();
			Util.log.info("Read CwebConfig.XML File End!");
		} catch (JDOMException e) {
			Util.log.error("Read CwebConfig.XML File Error!");
		} catch (IOException e) {
			Util.log.error("Read CwebConfig.XML File Error!");
		}
	}

	/**
	 * 存储各地配置信息
	 */
	private static ConcurrentHashMap<String, ServerObject> cityInfoByAreaCode = new ConcurrentHashMap<String, ServerObject>();

	/**
	 * CMPP发送线程
	 */
	private static ConcurrentHashMap<String, CmppSendThread> cmppSendHashMap = new ConcurrentHashMap<String, CmppSendThread>();

	/**
	 * CMPP接收线程
	 */
	private static ConcurrentHashMap<String, CmppReceiveThread> cmppReceiveHashMap = new ConcurrentHashMap<String, CmppReceiveThread>();

	/**
	 * CMPP链路检测线程
	 */
	private static ConcurrentHashMap<String, CmppActiveTestThread> cmppCheckHashMap = new ConcurrentHashMap<String, CmppActiveTestThread>();
}
