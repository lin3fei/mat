package org.test;

import com.chinamobile.onenet.sdk.DefaultOneNetClient;
import com.chinamobile.onenet.sdk.OneNetClient;
import com.chinamobile.onenet.sdk.OneNetException;
import com.chinamobile.onenet.sdk.entity.*;
import com.chinamobile.onenet.sdk.request.*;
import com.chinamobile.onenet.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 与OneNet平台进行数据交互的助手封装类。
 *
 * Created by WUXB on 2016-8-25.
 */
public class OneNetHelper {
	/**
	 * 记录日志
	 */
	private static Logger logger = LoggerFactory.getLogger(OneNetHelper.class);

	/**
	 * 设备ID
	 */
	private String deviceId;

	/**
	 * apikey
	 */
	private String apikey;

	/**
	 * OneNet平台API地址
	 */
	public static final String API_ADDRESS = "api.heclouds.com";

	/**
	 * 项目的apikey 9dcjFZixC5P3keAgsXIiHxN9yp4=
	 */
	public static final String MASTER_APIKEY = "PSMyYG1bmuYjxdwuV7pD90rPvCQ=";

	/**
	 * 构建一个新的OneNet实例
	 */
	public OneNetHelper() {

	}

	/**
	 * 构建一个新的OneNet助手实例
	 *
	 * @param deviceId
	 *            设备ID
	 * @param apikey
	 *            设备apikey
	 */
	public OneNetHelper(String deviceId, String apikey) {
		this.deviceId = deviceId;
		this.apikey = apikey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	/**
	 * 根据设备ID和设备apikey从OneNet平台上获取设备信息
	 *
	 * @return 如果没有获取到设备信息返回null
	 */
	public GetDeviceRsp.Data getDeviceData() {
		GetDeviceRsp.Data data = null;
		GetDeviceRequest getDeviceRequest = new GetDeviceRequest();
		getDeviceRequest.setApiKey(apikey);
		getDeviceRequest.setEntity(deviceId);
		try {
			GetDeviceRsp getDeviceRsp = clientOneNet().execute(getDeviceRequest);
			if (null != getDeviceRsp && getDeviceRsp.getErrno() == 0) {
				data = getDeviceRsp.getData();
			}
		} catch (OneNetException e) {
			logger.error("OneNet平台发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey, e);
		} catch (Exception e) {
			logger.error("从OneNet平台上获取设备数据发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey, e);
		}
		return data;
	}

	/**
	 * 根据设备ID和设备apikey从OneNet平台上获取设备所有的数据流
	 *
	 * @return 如果没有获取到设备信息或者没有获取到数据流信息返回null
	 */
	public List<DataStreamInfo> getDataStreamInfo() {
		GetDeviceRsp.Data data = getDeviceData();
		return null == data ? new ArrayList<DataStreamInfo>() : data.getDatastreams();
	}

	/**
	 * 创建设备ID
	 *
	 * @param deviceName
	 *            设备名称
	 * @param desc
	 *            描述
	 * @param protocol
	 *            协议
	 * @return 设备ID
	 */
	public String addDeviceId(String deviceName, String desc, String protocol) {
		AddDeviceReq req = new AddDeviceReq();
		req.setTitle(deviceName);
		req.setDesc(desc);
		req.setPrivate(true);
		req.setProtocol(protocol);

		AddDeviceRequest request = new AddDeviceRequest();
		request.setApiKey(MASTER_APIKEY);
		request.setEntity(req);

		AddDeviceRsp result = null;
		try {
			result = clientOneNet().execute(request);
		} catch (OneNetException e) {
			logger.error("向OneNet平台申请创建设备发生异常，设备名称=" + deviceName, e);
		}
		return (null == result) ? "" : result.getData().getDevice_id();
	}

	/**
	 * 创建设备ApiKey
	 *
	 * @param deviceId
	 *            设备ID
	 * @param title
	 *            apikey标题
	 * @return apikey
	 */
	public String addApiKey(String deviceId, String title) {
		Resource resource = new Resource();
		resource.setDev_id(deviceId);
		List<Resource> resources = new ArrayList<>(1);
		resources.add(resource);

		Permission permission = new Permission();
		permission.setResources(resources);
		List<Permission> permissions = new ArrayList<>(1);
		permissions.add(permission);

		AddKeyReq req = new AddKeyReq();
		req.setPermissions(permissions);
		req.setTitle(title);
		AddApiKeyRequest request = new AddApiKeyRequest();
		request.setApiKey(MASTER_APIKEY);
		request.setEntity(req);

		AddKeyRsp rsp = null;
		try {
			rsp = clientOneNet().execute(request);
		} catch (OneNetException e) {
			logger.error("向OneNet平台申请创建apikey发生异常，设备ID=" + deviceId, e);
		}
		return (null == rsp || null == rsp.getData()) ? "" : rsp.getData().getKey();
	}

	/**
	 * 添加数据流
	 * 
	 * @param deviceId
	 * @param apikey
	 * @param streamId
	 */
	public void addDataStreams(String deviceId, String apikey, String streamId) {
		AddDataStreamReq req = new AddDataStreamReq();
		req.setId(streamId);

		AddDataStreamRequest request = new AddDataStreamRequest();
		request.setDeviceID(deviceId);
		request.setApiKey(apikey);
		request.setEntity(req);

		try {
			AddDataStreamRsp result = clientOneNet().execute(request);
			logger.info(result.toString());
		} catch (OneNetException e) {
			logger.error("创建数据流发生异常，数据流id=" + streamId, e);
		}
	}

	public String getLatestDataStreamList(String stream) {
		SearchDataPointReq searchDataPointEntity = new SearchDataPointReq();
		searchDataPointEntity.setFirst((short) 0);
		searchDataPointEntity.setDataStreamID(stream);
		SearchDataPointRsp pointRsp = this.getSearchDataPointRsp(searchDataPointEntity);
		if (null == pointRsp) {
			return "";
		}

		List<DataStream> dataStreamList = pointRsp.getData().getDatastreams();
		if (null == dataStreamList || dataStreamList.isEmpty()) {
			return "";
		}

		List<DataPoint> dataPointList = dataStreamList.get(0).getDatapoints();
		if (null == dataPointList || dataPointList.isEmpty()) {
			return "";
		}

		return dataPointList.get(0).getValue().toString();
	}

	/**
	 * 获取数据流
	 *
	 * @param searchDataPointReq
	 *            SearchDataPointReq
	 * @return 返回数据点信息列表
	 */
	public SearchDataPointRsp getSearchDataPointRsp(SearchDataPointReq searchDataPointReq) {
		SearchDataPointRequest searchDataPointRequest = new SearchDataPointRequest();
		searchDataPointRequest.setDeviceID(deviceId);
		searchDataPointRequest.setApiKey(apikey);
		searchDataPointRequest.setEntity(searchDataPointReq);

		SearchDataPointRsp searchRsp = null;
		try {
			searchRsp = clientOneNet().execute(searchDataPointRequest);
		} catch (OneNetException e) {
			logger.error("OneNet平台发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey, e);
		} catch (Exception e) {
			logger.error("从OneNet平台上获取设备数据点发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey + "; 数据流ID="
					+ searchDataPointReq.getDataStreamID(), e);
		}

		return searchRsp;
	}

	/**
	 * 获取数据流
	 *
	 * @param searchDataPointReq
	 *            SearchDataPointReq
	 * @return 返回数据点信息列表
	 */
	public void getDataStreams(List<DataStream> dataStreamList, SearchDataPointReq searchDataPointReq) {
		SearchDataPointRsp pointRsp = this.getSearchDataPointRsp(searchDataPointReq);
		dataStreamList.addAll(pointRsp.getData().getDatastreams());
		String cursor = pointRsp.getData().getCursor();
		if (!StringUtils.isEmpty(cursor)) {
			searchDataPointReq.setCursor(cursor);
			getDataStreams(dataStreamList, searchDataPointReq);
		}
	}

	/**
	 * 获取数据流
	 *
	 * @param searchDataPointReq
	 *            SearchDataPointReq
	 * @return 返回数据点信息列表
	 */
	public List<DataStream> getDataStreams(SearchDataPointReq searchDataPointReq) {
		SearchDataPointRequest searchDataPointRequest = new SearchDataPointRequest();
		searchDataPointRequest.setDeviceID(deviceId);
		searchDataPointRequest.setApiKey(apikey);
		searchDataPointRequest.setEntity(searchDataPointReq);

		SearchDataPointRsp searchRsp = null;
		try {
			searchRsp = clientOneNet().execute(searchDataPointRequest);
		} catch (OneNetException e) {
			logger.error("OneNet平台发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey, e);
		} catch (Exception e) {
			logger.error("从OneNet平台上获取设备数据点发生异常，设备ID=" + deviceId + "; 设备apikey=" + apikey + "; 数据流ID="
					+ searchDataPointReq.getDataStreamID(), e);
		}

		return (null != searchRsp && searchRsp.getErrno() == 0) ? searchRsp.getData().getDatastreams()
				: new ArrayList<DataStream>();
	}

	/**
	 * 向设备发送指令
	 *
	 * @param commandValue
	 *            指令内容
	 * @return 发送结果
	 * @throws UnsupportedEncodingException
	 * @throws OneNetException
	 */
	public SendCommandRsp sendCommand(String commandValue) throws UnsupportedEncodingException, OneNetException {
		SendCommandRequest sendCommandRequest = new SendCommandRequest();
		sendCommandRequest.setApiKey(apikey);
		sendCommandRequest.setDeviceID(deviceId);
		sendCommandRequest.setEntity(commandValue.getBytes("UTF-8"));
		return clientOneNet().execute(sendCommandRequest);
	}

	/**
	 * 获取最近的数据点信息列表
	 *
	 * @param dataStreamID
	 *            数据流ID
	 * @param limit
	 *            获取的条数
	 * @return 数据点值
	 */
	public List<Object> getMostRecentDataPoints(String dataStreamID, int limit) {
		SearchDataPointReq searchDataPointEntity = new SearchDataPointReq();
		searchDataPointEntity.setDataStreamID(dataStreamID);
		searchDataPointEntity.setFirst((short) 0);
		searchDataPointEntity.setLimit(limit);

		List<Object> resultList = new ArrayList<>(limit);
		List<DataStream> streamList = this.getDataStreams(searchDataPointEntity);
		streamList.forEach(stream -> {
			List<DataPoint> dataPointList = stream.getDatapoints();
			dataPointList.forEach(point -> resultList.add(point.getValue()));
		});

		return resultList;
	}

	/**
	 * 获取OneNet的连接
	 *
	 * @return OneNet的连接
	 */
	private OneNetClient clientOneNet() {
		return new DefaultOneNetClient(API_ADDRESS);
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		String STREAM_ID = "vehPcbId,vehVoltage,vehGsu,vehLocation,vehTemperature,vehStation,sysStatus,ctrlFault,ctrlHardLock,ctrlSoftLock,ctrlCurrent,ctrlVoltage,CtrlHallCounter";
		// String STREAM_ID =
		// "vehPcbId,vehVoltage,vehGsu,vehLocation,vehTemperature,vehStation";
		OneNetHelper helper = new OneNetHelper("4068712", "1JG2sKqkRUImc2e8tAZFYZKe0Po=");
		// OneNetHelper helper = new OneNetHelper("4654566",
		// "V4w8G9f0Ar3RW3ct89WPZaGGAlE=");
		SearchDataPointReq searchDataPointEntity = new SearchDataPointReq();
		searchDataPointEntity.setDataStreamID(STREAM_ID);
		searchDataPointEntity.setFirst((short) 0);
		// searchDataPointEntity.setLimit(2);

		List<DataStream> streamList = helper.getDataStreams(searchDataPointEntity);
		streamList.forEach(stream -> {
			List<DataPoint> dataPointList = stream.getDatapoints();
			dataPointList.forEach(
					point -> System.out.println(stream.getId() + " = " + point.getAt() + " : " + point.getValue()));
		});
		System.out.println("耗时：" + (System.currentTimeMillis() - start) + "毫秒");
		/*
		 * Date startDate = DateUtil.strToDate("2016-11-02 06:51:19",
		 * "yyyy-MM-dd HH:mm:ss"); Date endDate =
		 * DateUtil.strToDate("2016-11-02 14:51:19", "yyyy-MM-dd HH:mm:ss");
		 * 
		 * int seconds = DateUtil.getTimeDelta(endDate, startDate); int size =
		 * seconds / 30 + 1; System.out.println("size=" + size);
		 * 
		 * OneNetHelper helper = new OneNetHelper("3266217",
		 * "qLARIE2rUJZWTLHi3BsHHilEbFU="); SearchDataPointReq
		 * searchDataPointEntity = new SearchDataPointReq();
		 * //searchDataPointEntity.setDataStreamID(
		 * "vehLocation,batVoltages,batTemperature,vehVoltage,batPackId,batCurrent,batSoc,vehTemperature,vehGsu,vehPcbId"
		 * ); //searchDataPointEntity.setLimit(6000);
		 * searchDataPointEntity.setStart(startDate);
		 * searchDataPointEntity.setEnd(endDate);
		 * 
		 * Long start = System.currentTimeMillis(); List<DataStream> streamList
		 * = helper.getDataStreams(searchDataPointEntity);
		 * streamList.forEach(stream -> { System.out.println(stream.getId() +
		 * ": = " + stream.getDatapoints().size()); });
		 * 
		 * 
		 * 
		 * System.out.println("从OneNet上获取数据耗时 " + (System.currentTimeMillis() -
		 * start) + "毫秒"); System.out.println(streamList.size());
		 */

	}
}
