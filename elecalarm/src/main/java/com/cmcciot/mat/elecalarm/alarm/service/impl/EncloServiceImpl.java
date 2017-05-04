package com.cmcciot.mat.elecalarm.alarm.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.alarm.AuthKeyConstant;
import com.cmcciot.mat.elecalarm.alarm.bean.AlertMessageBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EncloAlarmBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuLogBean;
import com.cmcciot.mat.elecalarm.alarm.bean.MultipleDataBean;
import com.cmcciot.mat.elecalarm.alarm.dao.EnclosuDAO;
import com.cmcciot.mat.elecalarm.alarm.service.EncloService;
import com.cmcciot.mat.elecalarm.common.DataPointUtil;
import com.cmcciot.mat.elecalarm.common.util.DateTools;
import com.cmcciot.mat.elecalarm.common.util.DistanceTool;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.locationmanager.bean.DeviceBean;
import com.cmcciot.mat.elecalarm.locationmanager.dao.DeviceDAO;
import com.cmcciot.mat.elecalarm.locationmanager.service.LocationService;
import com.cmcciot.mat.elecalarm.locationmanager.util.LbsPropertyTools;
import com.cmcciot.mat.elecalarm.sdhttp.service.OneNetDataPointService;
import com.cmcciot.mat.elecalarm.sdhttp.service.PushSmsService;

@Service("encloService")
public class EncloServiceImpl implements EncloService {
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private OneNetDataPointService oneNetDataPointService;
	
	@Resource
	private LocationService locationService;
	
	@Resource
	private EnclosuDAO enclosuDAO;
	
	@Resource
	private DeviceDAO deviceDAO;
	
	@Resource
	private PushSmsService pushSmsService;

	@SuppressWarnings("unchecked")
	@Override
	public void encloResend(int encloId, String at) {
		try {
			logger.info("进入电子围栏告警业务");
			boolean status = true;
			while (status) {
				logger.info("处理业务，准备发送告警！"+"围栏存储map:" +AuthKeyConstant.stringObjectMap.get(String.valueOf(encloId)));
				logger.info("处理业务，准备发送告警！");
				// 定位类型
				String type = "";
				// 电量
				int battery = 0;
				// 信号强度
				int gsm = 0;
				String alertInfo ="";
				EnclosuBean eb = enclosuDAO.findDevice(encloId);
				// 再次扫描该设备
				if (StringTools.isEmptyOrNull(eb)) {
					break;
				}
				String deviceId = String.valueOf(eb.getDeviceId());
				// 对该设备进行时间判断 格式为10:00类型
				String startTime = eb.getStartTime();
				String endTime = eb.getEndTime();
				// 当前时间在此区间内继续执行，否则跳出当前执行
				if (DateTools.compareHM(startTime, endTime)) {
					break;
				}
				int n_duration =0;
				int h_startTime =0;
				int m_startTime =0;
				int h_endTime =0 ;
				int m_endTime =0 ;
				h_startTime=Integer.valueOf(startTime.substring(0, 2));//获取小时数（开始）
				h_endTime =Integer.valueOf(endTime.substring(0, 2));//获取小时数（结束）
				m_startTime =Integer.valueOf(startTime.substring(3, 5));//获取分钟数（开始）
				m_endTime =Integer.valueOf(endTime.substring(3, 5));//获取分钟数（结束）
				if(m_endTime>m_startTime){

					n_duration = (h_endTime - h_startTime) * 60 +(m_endTime - m_startTime);
				}else{

					n_duration= (h_endTime - h_startTime  -1 ) * 60 +(m_endTime + 60 - m_startTime);
				}
				//设置频率
				double freq = 1000*60*15;
				//围栏的持续时间
				double duration = 1000*60*n_duration;
				//设置围栏持续时间
				double resetAs = 1000*60*30;

				// 对设备星期的判断
				char[] week = eb.getWeekTime().toCharArray();
				if ("0".equals(String.valueOf(week[DateTools.getWeekOfDate(new Date())]))) {
					break;
				}
				// 向设备云拉取数据
				DeviceBean db = deviceDAO.findDeviceById(deviceId);
				if (StringTools.isEmptyOrNull(db)) {
					break;
				}
				String sdDeviceId = db.getSd_device_id();
				String jsonStr = DataPointUtil.getDataPoint(sdDeviceId);
				// 初始化经纬度
				double lat = 0.0;
				double lon = 0.0;
				String atTime = "";
				// 对返回json进行判断
				if (StringTools.isEmptyOrNull(jsonStr)) {
					break;
	        	} else {
	        		Map<String, Object> mapData = new HashMap<String, Object>();
	            	mapData = JacksonUtil.jsonToMap(jsonStr);
	            	
					List<Map<String, Object>> datastreams = (List<Map<String, Object>>) mapData.get("datastreams");
					if (StringTools.isEmptyOrNull(datastreams) || datastreams.size() == 0) {
						break;
					}
	            	for (Map<String, Object> datapoints : datastreams) {
	            		List<Map<String, Object>> data = (List<Map<String, Object>>) datapoints.get("datapoints");
	            		for (Map<String, Object> value : data) {
	            			Map<String, Object> valueData = (Map<String, Object>) value.get("value");
	            			atTime = (String) value.get("at");
	            			atTime.replace("T", " ");
	            			type = (String) valueData.get("type");
	            			gsm = (Integer) valueData.get("gsm");
	            			battery = (Integer) valueData.get("battery");
	            			alertInfo = JacksonUtil.mapToJson(valueData);
	                        if ("GPS".equals(type))
	                        {
	                        	lat = Double.valueOf((Double) valueData.get("lat"));
	                            lon = Double.valueOf((Double) valueData.get("lon"));
	                        }
	                        else if ("LBS".equals(type))
	                        {
	                        	Map<String, Object> paramMap = new HashMap<String, Object>();
	    	                    paramMap.put("IMEI", db.getDevice_IMEI());
	    	                    paramMap.put("IMSI", db.getDevice_imsi());
	                        	List<Map<String, Object>> celltowersList = (List<Map<String, Object>>) valueData.get("LBSDATAS");
	                        	// 获取高德地图的serverIp
	    	        			String serverip = (String) valueData.get("serverip");
	    	                    paramMap.put("serverip", serverip);
	                            String gpsStr = "";
	                            // 转换LBS到GPS
	                            if (!StringTools.isEmptyOrNull(celltowersList) && celltowersList.size() > 0) {
	                            	if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGps"))) {
	    	                    		paramMap.put("LBS", celltowersList);
	    	                            gpsStr = locationService.getGPSLocationByGaode(paramMap);
	    	                    	} else {
	    	                    		gpsStr = locationService.getGPSLocationByMixLBS(0, celltowersList);
	    	                    	}
	                            } else {
	                            	Integer mcc = (Integer) valueData.get("mcc");
	                                Integer mnc = (Integer) valueData.get("mnc");
	                                Integer lac = (Integer) valueData.get("lac");
	                                Integer cid = (Integer) valueData.get("cid");
	                                if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGps"))) {
	    	                        	List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
	    	                            Map<String, Object> m = new HashMap<String, Object>();
	    	                            m.put("mcc", Integer.valueOf(mcc));
	    	                            m.put("mnc", Integer.valueOf(mnc));
	    	                            m.put("lac", Integer.valueOf(lac));
	    	                            m.put("cid", Integer.valueOf(cid));
	    	                            temp.add(m);
	    	                            paramMap.put("LBS", temp);
	    	                            gpsStr = locationService.getGPSLocationByGaode(paramMap);
	    	                    	} else {
	    	                    		gpsStr = locationService.getGPSLocationByLBS(Integer.valueOf(mcc),
	    	                                    Integer.valueOf(mnc),
	    	                                    Integer.valueOf(lac),
	    	                                    Integer.valueOf(cid));
	    	                    	}
	                            }
	                            Map<String, Object> gpsMap = JacksonUtil.jsonToMap(gpsStr);
	                            logger.info("通过开关判断是否需要转高德经纬度~~");
	                            if("on".equals(LbsPropertyTools.getValue("lbs.gaodeGpsVal"))){
	                            	logger.info("转高德经纬度~~");
	                            	lat = Double.valueOf((Double) gpsMap.get("gaode_lat"));
		                            lon = Double.valueOf((Double) gpsMap.get("gaode_lng"));
	                            }else{
	                            lat = Double.valueOf((Double) gpsMap.get("lat"));
	                            lon = Double.valueOf((Double) gpsMap.get("lng"));}
	                        }
	            		}
	            	}
	        	}
				// 获取围栏设置好的圆心，半径
            	int enclosuRadius = eb.getEnclosuRadius();
            	Map<String, Object> centerMap = 
            			(Map<String, Object>) JacksonUtil.jsonToMap(eb.getEnclosuCenter());
            	// 通过经纬度获取设备到圆心的距离 并与半径作比较
            	if (StringTools.isEmptyOrNull((Double) centerMap.get("lat")) || 
            			StringTools.isEmptyOrNull((Double) centerMap.get("lon"))) {
            		break;
            	}
            	double radius = 0;
            	logger.info("开始判断圆心是否需要转高德经纬度~~");
            	if("on".equals(LbsPropertyTools.getValue("lbs.gaodeGpsVal"))){
	            		logger.info("圆心开始高德经纬度转换~~");
	            		Map<String, Object> map =new HashMap<String, Object>();
	            		map.put("lat", centerMap.get("lat"));
	            		map.put("lng", centerMap.get("lon"));
	            		JSONObject modelStr = locationService.localGpsToGaodeGps(map);
	            		radius = DistanceTool.GetDistance(lat, lon,
	    		            	Double.valueOf((Double) modelStr.get("lat")),
	    		            	Double.valueOf((Double) modelStr.get("lng")));
	            	}else{
	            			radius = DistanceTool.GetDistance(lat, lon,
	            			Double.valueOf((Double) centerMap.get("lat")),
	            			Double.valueOf((Double) centerMap.get("lon")));}
            	// 设置的半径小于两点之间的距离，将产生报警
            	EncloAlarmBean eab = enclosuDAO.findAlarm(encloId);
            	if (at.equals(atTime) && eab.getAlarmTimes() >= 2) {

					logger.info("围栏ID为:"+eab.getEncloId()+"---更新次数:" + (eab.getAlarmTimes())+"数据点最后时间为"+atTime+"退出break");
            		break;
            	}
				//重新赋值上次数据点时间
				at = atTime;
				if (!StringTools.isEmptyOrNull(eab) && (radius - enclosuRadius) >= 0) {
					if (eab.getAlarmTimes() <= 3) {
						String time = String.valueOf(System.currentTimeMillis());
						// 发送推送消息和短信
						pushSmsService.pushSms(deviceId, lon, lat, String.valueOf(encloId), type, gsm, battery, time);
						if (eab.getAlarmTimes() == 1) {
//							pushSmsService.RegularReportingsettings(deviceId,
//									freq, duration, resetAs);
					}
						// 更新次数
						update(encloId, (eab.getAlarmTimes() + 1));
						logger.info("围栏ID为:"+eab.getEncloId()+"---更新次数:" + (eab.getAlarmTimes() + 1));
						// 添加日志
						EnclosuLogBean enclosuLogBean = new EnclosuLogBean();
						enclosuLogBean.setEnclosuId(encloId);
						enclosuLogBean.setDeviceId(deviceId);
						enclosuLogBean.setAlarmInfo("越界告警");
						enclosuLogBean.setAlarmTimes(String.valueOf(eab.getAlarmTimes()));
						enclosuLogBean.setCreatTime(time);
						enclosuDAO.addAlarmLog(enclosuLogBean);
						Map<String, Object> encloMap = (Map<String, Object>) JacksonUtil.jsonToMap(alertInfo);
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			            String timeTemp = format.format(Long.valueOf(time));
	                    encloMap.put("alertType", "1");
	                    encloMap.remove("LBSDATAS");
	                    encloMap.put("lon", lon);
	                    encloMap.put("lat", lat);
	                    encloMap.put("time", timeTemp);
						// 添加日志到告警信息表
						AlertMessageBean amb = new AlertMessageBean();
						amb.setAlertDeviceId(deviceId);
						amb.setAlertTime(time);
						amb.setSaveTime(time);
						amb.setAlertIndex("1");
						amb.setAlertInfo(JacksonUtil.mapToJson(encloMap));
						amb.setEnclosureId(encloId);
						enclosuDAO.addAlertMessage(amb);
						// 将上报通知存入综合数据表
	                    MultipleDataBean multipleDataBean = new MultipleDataBean();
	                    multipleDataBean.setDeviceId(deviceId);
	                    multipleDataBean.setType("alarm");
	                    multipleDataBean.setTime(time);
	                    multipleDataBean.setData(JacksonUtil.mapToJson(encloMap));
	                    enclosuDAO.addMultipleData(multipleDataBean);
						if (eab.getAlarmTimes() >= 3) {
							status = false;
							logger.info("删除对应信息！");
							delete(encloId);
							AuthKeyConstant.stringObjectMap.remove(String.valueOf(encloId));
							logger.info("更新围栏信息暂停状态位！");
							updateEncloInfo(encloId);
						}
					}
				} else {
					break;
				}
				Thread.sleep(1000 * 60 * 16);
			}
		} catch (Exception e) {
			// 删除该围栏id的消息记录
			logger.error("定时告警错误", e);
			delete(encloId);
		}
	}
	
	public synchronized void update(int encloId, int times) {
		enclosuDAO.updateAlarm(encloId, times);
	}
	
	public synchronized void updateEncloInfo(int encloId) {
		enclosuDAO.updateEncloInfo(encloId, 1);
	}
	
	public synchronized void delete(int encloId) {
		enclosuDAO.deleteAlarm(encloId);
	}
}
