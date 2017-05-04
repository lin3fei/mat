/**  
* 2015-8-26  
* author:zoujiang
*/

package com.cmcciot.mat.enclosure.job;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmcciot.mat.enclosure.business.userManager.service.EnClosuerService;
import com.cmcciot.mat.enclosure.tools.PropertyUtil;
import com.cmcciot.mat.enclosure.tools.StringTools;

public class EnclosureJob {

	@Autowired
	private EnClosuerService enClosuerService;

	private static Logger logger = LoggerFactory.getLogger(EnclosureJob.class);

	/**
	 * 超出围栏范围而触发告警
	 */
	public void execute() {

		logger.info("EnclosureJob is running..");
		// 查询当前时间正在生效的围栏

		try {
			List<Map<String, Object>> list = enClosuerService.queryInvaildEnClosure();
			logger.info("list.size():" + list.size());
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> bean = list.get(i);
					String deviceid = bean.get("DEVICEID").toString();
					String jsonStr = getDataPoint(deviceid);
					if (StringTools.isEmptyOrNull(jsonStr)) {
						continue;
					} else {
						new EnclosureThread(jsonStr, bean, enClosuerService).start();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getDataPoint(String deviceid) {
		String jsonStr = "";
		try {
			jsonStr = queryTdDataPoint(deviceid, "laorendingwei");
		} catch (Exception e) {
			logger.info("调用设备云查询数据失败！", e);
		}
		return jsonStr;
	}

	/**
	 * 查询终端设备的数据点
	 * 
	 * @param deviceID
	 *            设备ID
	 * @param datastreamId
	 *            数据流ID(不传默认为该设备类型所有数据流)
	 * 
	 * @return String [数据流]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String queryTdDataPoint(String deviceID, String datastreamId) {
		String jsonStr = "";
		try {
			// 通过传参拼接参数
			StringBuffer urlParamSb = new StringBuffer();

			// 当isLastSingle为0时
			if (!StringTools.isEmptyOrNull(datastreamId)) {
				urlParamSb.append("&datastream_id=" + datastreamId);
			}
			urlParamSb.append("&limit=1");
			String uri = PropertyUtil.getValue("oneNetHttpSite") + "devices/" + deviceID + "/datapoints" + "?"
					+ urlParamSb.substring(1).toString();
			logger.info("转设备云数据开始：" + System.currentTimeMillis() + "+++++++++++++++++++++++++++++++++");
			String content = httpGetOneNet(uri, PropertyUtil.getValue("oneNetApiKey"));
			logger.info("转设备云数据结束：" + System.currentTimeMillis() + "+++++++++++++++++++++++++++++++++");
			// 返回实例：{"errno":0,"data":{"count":1,"datastreams":[{"datapoints":[{"at":"2015-08-24
			// 17:26:04.000","value":{"gsm":63,"DTP":1800,"PerRpt":"300|0|0","DTT":600,"GPSstate":3,"serverip":"10.32.103.1","battery":8,"type":"LBS","LBSDATAS":[{"ss":-75,"mnc":0,"mcc":460,"cid":42341,"lac":13204},{"ss":-82,"mnc":0,"mcc":460,"cid":42343,"lac":13204},{"ss":-84,"mnc":0,"mcc":460,"cid":42342,"lac":13204}]}}],"id":"laorendingwei"}]},"error":"succ"}

			if (StringTools.isEmptyOrNull(content)) {
				logger.info("未查到指定设备的数据流，错误信息是：返回数据为空！");
				return null;
			} else {
				JSONObject jsonObj = JSONObject.fromObject(content).optJSONObject("data");
				jsonStr = jsonObj.toString();
			}
		} catch (Exception e) {
			logger.info("查询数据点错误，错误信息是：" + e.toString());
		}
		// 测试
		// jsonStr =
		// "{\"count\":1,\"datastreams\":[{\"datapoints\":[{\"at\":\"2015-08-24
		// 17:26:04.000\",\"value\":{\"gsm\":63,\"DTP\":1800,\"PerRpt\":\"300|0|0\",\"DTT\":600,\"GPSstate\":3,\"serverip\":\"10.32.103.1\",\"battery\":8,\"type\":\"LBS\",\"LBSDATAS\":[{\"ss\":-75,\"mnc\":0,\"mcc\":460,\"cid\":42341,\"lac\":13204},{\"ss\":-82,\"mnc\":0,\"mcc\":460,\"cid\":42343,\"lac\":13204},{\"ss\":-84,\"mnc\":0,\"mcc\":460,\"cid\":42342,\"lac\":13204}]}}],\"id\":\"laorendingwei\"}]}";

		return jsonStr;
	}

	public static String httpGetOneNet(String uri, String apiKey) {
		HttpClient httpClient = new DefaultHttpClient();
		logger.info("调用接口地址是：" + uri);
		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("api-key", apiKey);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				String conResult = EntityUtils.toString(response.getEntity());
				logger.info("调用设备云接口成功，返回报文是：" + conResult);
				conResult = conResult.replace("\"data\":[]", "\"data\":{}").replace("\"other\":[]", "\"other\":{}");
				return conResult;
			} else {
				logger.info("调用onenet http接口异常！返回的状态码是：" + response.getStatusLine().getStatusCode() + "=="
						+ EntityUtils.toString(response.getEntity()));
			}
		} catch (Exception e) {
			logger.info("调用接口发生异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
		}
		return "";
	}
}
