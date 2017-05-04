package com.cmcciot.mat.elecalarm.alarm.execute;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.alarm.bean.AlertMessageBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EncloAlarmBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuLogBean;
import com.cmcciot.mat.elecalarm.alarm.common.LockEncloId;
import com.cmcciot.mat.elecalarm.alarm.dao.EnclosuDAO;
import com.cmcciot.mat.elecalarm.common.DataPointUtil;
import com.cmcciot.mat.elecalarm.common.util.DateTools;
import com.cmcciot.mat.elecalarm.common.util.DistanceTool;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.locationmanager.bean.DeviceBean;
import com.cmcciot.mat.elecalarm.locationmanager.dao.DeviceDAO;
import com.cmcciot.mat.elecalarm.locationmanager.service.LocationService;
import com.cmcciot.mat.elecalarm.sdhttp.service.OneNetDataPointService;
import com.cmcciot.mat.elecalarm.sdhttp.service.PushSmsService;

@Service("enclosuTread")
public class EnclosuTread implements Runnable, Serializable, IEnclosuTread {

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private int encloId;
	
	private static LockEncloId lockEncloId = new LockEncloId();
	
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
	public void run() {
		try {
			logger.info("进入电子围栏告警业务");
			boolean status = true;
			lockEncloId.setEncloId(encloId);
			while (status) {
				logger.info("处理业务，准备发送告警！");
				synchronized (lockEncloId) {
					// 定位类型
					String type = "";
					// 电量
					int battery = 0;
					// 信号强度
					int gsm = 0;
					String alertInfo ="";
					EnclosuBean eb = enclosuDAO.findDevice(lockEncloId.getEncloId());
					String deviceId = String.valueOf(eb.getDeviceId());
					// 再次扫描该设备
					if (StringTools.isEmptyOrNull(eb)) {
						break;
					}
					// 对设备星期的判断
					char[] week = eb.getWeekTime().toCharArray();
					if ("0".equals(week[DateTools.getWeekOfDate(new Date())])) {
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
		            			type = (String) valueData.get("type");
		            			gsm = (Integer) valueData.get("gsm");
		            			battery = (Integer) valueData.get("battery");
		            			alertInfo = JacksonUtil.mapToJson(valueData);
		                        if ("GPS".equals(type))
		                        {
		                        	lat = Double.valueOf((String) valueData.get("lat"));
		                            lon = Double.valueOf((String) valueData.get("lon"));
		                        }
		                        else if ("LBS".equals(type))
		                        {
		                        	List<Map<String, Object>> celltowersList = (List<Map<String, Object>>) valueData.get("LBSDATAS");
		                            String gpsStr = "";
		                            // 转换LBS到GPS
		                            if (!StringTools.isEmptyOrNull(celltowersList) && celltowersList.size() > 0) {
		                            	gpsStr = locationService.getGPSLocationByMixLBS(0, celltowersList);
		                            } else {
		                            	Integer mcc = (Integer) valueData.get("mcc");
		                                Integer mnc = (Integer) valueData.get("mnc");
		                                Integer lac = (Integer) valueData.get("lac");
		                                Integer cid = (Integer) valueData.get("cid");
		                            	gpsStr = locationService.getGPSLocationByLBS(Integer.valueOf(mcc), Integer.valueOf(mnc),
		                                		Integer.valueOf(lac), Integer.valueOf(cid));
		                            }
		                            Map<String, Object> gpsMap = JacksonUtil.jsonToMap(gpsStr);
		                            lat = Double.valueOf((Double) gpsMap.get("lat"));
		                            lon = Double.valueOf((Double) gpsMap.get("lng"));
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
	            	double radius = DistanceTool.GetDistance(lat, lon,
	            			Double.valueOf((Double) centerMap.get("lat")),
	            			Double.valueOf((Double) centerMap.get("lon")));
	            	// 设置的半径小于两点之间的距离，将产生报警
	            	EncloAlarmBean eab = enclosuDAO.findAlarm(lockEncloId.getEncloId());
					if (!StringTools.isEmptyOrNull(eab) && (radius - enclosuRadius) >= 0) {
						if (eab.getAlarmTimes() <= 3) {
							String time = String.valueOf(System.currentTimeMillis());
							// 发送推送消息和短信
							pushSmsService.pushSms(deviceId, lon, lat, String.valueOf(lockEncloId.getEncloId()), type, gsm, battery, time);
							// 更新次数
							update(lockEncloId.getEncloId(), (eab.getAlarmTimes() + 1));
							logger.info("更新次数" + (eab.getAlarmTimes() + 1));
							// 添加日志
							EnclosuLogBean enclosuLogBean = new EnclosuLogBean();
							enclosuLogBean.setEnclosuId(lockEncloId.getEncloId());
							enclosuLogBean.setDeviceId(deviceId);
							enclosuLogBean.setAlarmInfo("越界告警");
							enclosuLogBean.setAlarmTimes(String.valueOf(eab.getAlarmTimes()));
							enclosuLogBean.setCreatTime(time);
							enclosuDAO.addAlarmLog(enclosuLogBean);
							// 添加日志到告警信息表
							AlertMessageBean amb = new AlertMessageBean();
							amb.setAlertDeviceId(deviceId);
							amb.setAlertTime(time);
							amb.setSaveTime(time);
							amb.setAlertIndex("1");
							amb.setAlertInfo(alertInfo);
							amb.setEnclosureId(encloId);
							enclosuDAO.addAlertMessage(amb);
							
							
							if (eab.getAlarmTimes() >= 3) {
								status = false;
								logger.info("删除对应信息！");
								delete(lockEncloId.getEncloId());
								logger.info("更新围栏信息暂停状态位！");
								updateEncloInfo(lockEncloId.getEncloId());
							}
						}
					} else {
						break;
					}
				}
				Thread.sleep(1000 * 60 * 16);
			}
		} catch (Exception e) {
			// 删除该围栏id的消息记录
			logger.error("定时告警错误", e);
			delete(lockEncloId.getEncloId());
		} finally {
			// 删除该围栏id的消息记录
//			delete(encloId);
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

	@Override
	public void setValue(int encloId) {
		this.encloId = encloId;
	}
}
