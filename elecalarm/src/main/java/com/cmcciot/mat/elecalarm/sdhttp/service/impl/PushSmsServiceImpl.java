package com.cmcciot.mat.elecalarm.sdhttp.service.impl;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;
import com.cmcciot.mat.elecalarm.sdhttp.service.PushSmsService;

/**
 * 用于调用MAT平台发送短信和推送<一句话功能简述>
 * <功能详细描述>
 * 
 * @author fh
 * @version  [版本号, 2015年4月7日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */

@Service("pushSmsService")
public class PushSmsServiceImpl implements PushSmsService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void pushSms(String deviceId, double lon, double lat, String encloId, String type, int gms, int battery, String time) {
		try {
			String httpSite = PropertyUtil.getValue("httpPlatform");
			String url = httpSite + "platform/pushSms?deviceId=" + deviceId + "&encloId=" + encloId + "&lon=" + lon + "&lat=" + lat
					+ "&type=" + type + "&gms=" + gms + "&battery=" + battery + "&time=" + time;
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			client.execute(get);
		} catch (Exception e) {
			logger.error("调用推送短信失败", e);
			try {
				throw new Exception("调用推送短信失败");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void RegularReportingsettings(String deviceId, int freq,int duration, int resetAs)
	{
		try {
			String httpSite = PropertyUtil.getValue("httpPlatform");
			String url = httpSite + "platform/regularReportingsettings?deviceID=" + deviceId + "&freq=" + freq;

			//注释参数20150804			+ "&duration=" + duration + "&resetAs=" + resetAs
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			client.execute(get);
		} catch (Exception e) {
			logger.error("设备定期上报参数设置失败", e);
			try {
				throw new Exception("设备定期上报参数设置失败");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
