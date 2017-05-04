package com.cmcciot.mat.elecalarm.alarm.execute;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import com.cmcciot.mat.elecalarm.alarm.bean.AlertMessageBean;
import com.cmcciot.mat.elecalarm.alarm.bean.CacheObjectBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EncloAlarmBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuLogBean;
import com.cmcciot.mat.elecalarm.alarm.bean.MultipleDataBean;
import com.cmcciot.mat.elecalarm.alarm.dao.EnclosuDAO;
import com.cmcciot.mat.elecalarm.alarm.service.impl.EncloServiceImpl;
import com.cmcciot.mat.elecalarm.common.DataPointUtil;
import com.cmcciot.mat.elecalarm.common.ElecAlarmConstant;
import com.cmcciot.mat.elecalarm.common.util.DateTools;
import com.cmcciot.mat.elecalarm.common.util.DistanceTool;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.SpringApplicationContextHolder;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.locationmanager.bean.DeviceBean;
import com.cmcciot.mat.elecalarm.locationmanager.dao.DeviceDAO;
import com.cmcciot.mat.elecalarm.locationmanager.service.LocationService;
import com.cmcciot.mat.elecalarm.locationmanager.util.LbsPropertyTools;
import com.cmcciot.mat.elecalarm.sdhttp.service.OneNetDataPointService;
import com.cmcciot.mat.elecalarm.sdhttp.service.PushSmsService;

public class EnclosuExcuteThread implements Runnable
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
    
    @Resource
    private EncloServiceImpl encloService;
    
    private int encloId;
    
    public EnclosuExcuteThread(int encloId)
    {
        this.encloId = encloId;
        this.oneNetDataPointService = (OneNetDataPointService) SpringApplicationContextHolder.getBean("oneNetDataPointService");
        this.locationService = (LocationService) SpringApplicationContextHolder.getBean("locationService");
        this.enclosuDAO = (EnclosuDAO) SpringApplicationContextHolder.getBean("enclosuDAO");
        this.deviceDAO = (DeviceDAO) SpringApplicationContextHolder.getBean("deviceDAO");
        this.pushSmsService = (PushSmsService) SpringApplicationContextHolder.getBean("pushSmsService");
        this.encloService = (EncloServiceImpl) SpringApplicationContextHolder.getBean("encloService");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void run()
    {
        try
        {
            logger.info("进入围栏ID为:" + encloId + "的电子围栏告警业务");
            CacheObjectBean cob = (CacheObjectBean) ElecAlarmConstant.threadMemoryMap.get(String.valueOf(encloId));
            if (StringTools.isEmptyOrNull(cob))
            {
                logger.info("线程消息寄存器查询不到围栏ID为：" + encloId + "的线程，退出本线程");
                return;
            }
            
            // 定位类型
            String type = "";
            // 电量
            int battery = 0;
            // 信号强度
            int gsm = 0;
            String alertInfo = "";
            EnclosuBean eb = enclosuDAO.findDevice(encloId);
            if (StringTools.isEmptyOrNull(eb))
            {
                logger.info("没有查询到围栏ID为：" + encloId + "的围栏信息");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            String deviceId = String.valueOf(eb.getDeviceId());
            // 对该设备进行时间判断 格式为10:00类型
            String startTime = eb.getStartTime();
            String endTime = eb.getEndTime();
            // 当前时间在此区间内继续执行，否则跳出当前执行
            if (DateTools.compareHM(startTime, endTime))
            {
                logger.info("该围栏不在有效时间内");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            // 对设备星期的判断
            char[] week = eb.getWeekTime().toCharArray();
            if ("0".equals(String.valueOf(week[DateTools.getWeekOfDate(new Date())])))
            {
                logger.info("该围栏不在有效时间内");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            //进行15分钟判断，判断当前时间和上一次触发告警的时间间隔是否大于15分钟。
            CacheObjectBean cob1 = (CacheObjectBean) ElecAlarmConstant.elecAlarmMemoryMap.get(String.valueOf(encloId));
            long nowTime = System.currentTimeMillis();
            //cob1为空，表示第一次触发告警，不需要判断时间间隔
            if (!StringTools.isEmptyOrNull(cob1))
            {
                long lastAlarmTime = cob1.getLastAlarmTime();
                if ((nowTime - lastAlarmTime) < 900000)
                {
                    logger.info("和上一次告警时间相差不到15分钟，围栏ID为：" + encloId
                            + "上次告警时间为：" + lastAlarmTime + "。现在时间是：" + nowTime);
                    ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                    return;
                }
            }
            
            // 向设备云拉取数据
            DeviceBean db = deviceDAO.findDeviceById(deviceId);
            if (StringTools.isEmptyOrNull(db))
            {
                logger.info("围栏ID为：" + encloId + "的围栏对应的设备ID为：" + deviceId
                        + "的设备不存在");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            String sdDeviceId = db.getSd_device_id();
            String jsonStr = DataPointUtil.getDataPoint(sdDeviceId);
            // 初始化经纬度
            double lat = 0.0;
            double lon = 0.0;
            String atTime = "";
            // 对返回json进行判断
            if (StringTools.isEmptyOrNull(jsonStr))
            {
                logger.info("围栏ID为：" + encloId + "的围栏对应的设备ID为：" + deviceId
                        + "的设备查询数据点信息失败");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            else
            {
                Map<String, Object> mapData = new HashMap<String, Object>();
                mapData = JacksonUtil.jsonToMap(jsonStr);
                
                List<Map<String, Object>> datastreams = (List<Map<String, Object>>) mapData.get("datastreams");
                if (StringTools.isEmptyOrNull(datastreams)
                        || datastreams.size() == 0)
                {
                    logger.info("解析围栏ID为：" + encloId + "的围栏对应的设备ID为："
                            + deviceId + "的设备查询数据点信息失败,信息是：" + jsonStr);
                    ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                    return;
                }
                for (Map<String, Object> datapoints : datastreams)
                {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) datapoints.get("datapoints");
                    for (Map<String, Object> value : data)
                    {
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
                            if (!StringTools.isEmptyOrNull(celltowersList)
                                    && celltowersList.size() > 0)
                            {
                                if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGps")))
                                {
                                    paramMap.put("LBS", celltowersList);
                                    gpsStr = locationService.getGPSLocationByGaode(paramMap);
                                }
                                else
                                {
                                    gpsStr = locationService.getGPSLocationByMixLBS(0,
                                            celltowersList);
                                }
                            }
                            else
                            {
                                Integer mcc = (Integer) valueData.get("mcc");
                                Integer mnc = (Integer) valueData.get("mnc");
                                Integer lac = (Integer) valueData.get("lac");
                                Integer cid = (Integer) valueData.get("cid");
                                if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGps")))
                                {
                                    List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
                                    Map<String, Object> m = new HashMap<String, Object>();
                                    m.put("mcc", Integer.valueOf(mcc));
                                    m.put("mnc", Integer.valueOf(mnc));
                                    m.put("lac", Integer.valueOf(lac));
                                    m.put("cid", Integer.valueOf(cid));
                                    temp.add(m);
                                    paramMap.put("LBS", temp);
                                    gpsStr = locationService.getGPSLocationByGaode(paramMap);
                                }
                                else
                                {
                                    gpsStr = locationService.getGPSLocationByLBS(Integer.valueOf(mcc),
                                            Integer.valueOf(mnc),
                                            Integer.valueOf(lac),
                                            Integer.valueOf(cid));
                                }
                            }
                            Map<String, Object> gpsMap = JacksonUtil.jsonToMap(gpsStr);
                            logger.info("通过开关判断是否需要转高德经纬度~~");
                            if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGpsVal")))
                            {
                                logger.info("转高德经纬度~~");
                                lat = Double.valueOf((Double) gpsMap.get("gaode_lat"));
                                lon = Double.valueOf((Double) gpsMap.get("gaode_lng"));
                            }
                            else
                            {
                                lat = Double.valueOf((Double) gpsMap.get("lat"));
                                lon = Double.valueOf((Double) gpsMap.get("lng"));
                            }
                        }
                    }
                }
            }
            // 获取围栏设置好的圆心，半径
            int enclosuRadius = eb.getEnclosuRadius();
            Map<String, Object> centerMap = (Map<String, Object>) JacksonUtil.jsonToMap(eb.getEnclosuCenter());
            // 通过经纬度获取设备到圆心的距离 并与半径作比较
            if (StringTools.isEmptyOrNull((Double) centerMap.get("lat"))
                    || StringTools.isEmptyOrNull((Double) centerMap.get("lon")))
            {
                logger.info("解析围栏ID为：" + encloId + "的围栏对应的圆心信息有错");
                ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                return;
            }
            double radius = 0;
            
            String gaodeGpsVal = LbsPropertyTools.getValue("lbs.gaodeGpsVal");
            logger.info("开始判断圆心是否需要转高德经纬度:" + gaodeGpsVal);
            
            if ("on".equals(gaodeGpsVal))
            {
                logger.info("圆心开始高德经纬度转换~~");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("lat", centerMap.get("lat"));
                map.put("lng", centerMap.get("lon"));
                JSONObject modelStr = locationService.localGpsToGaodeGps(map);
                radius = DistanceTool.GetDistance(lat,
                        lon,
                        Double.valueOf((Double) modelStr.get("lat")),
                        Double.valueOf((Double) modelStr.get("lng")));
            }
            else
            {
                radius = DistanceTool.GetDistance(lat,
                        lon,
                        Double.valueOf((Double) centerMap.get("lat")),
                        Double.valueOf((Double) centerMap.get("lon")));
            }
            //告警次数记录表
            EncloAlarmBean eab = enclosuDAO.findAlarm(encloId);
            
            // 设置的半径小于两点之间的距离，将产生报警
            if ((radius - enclosuRadius) >= 0)
            {
                
                if (!isAlam(eab,
                        cob1,
                        encloId,
                        atTime,
                        nowTime,
                        eb.getCreateTime(),
                        eb.getStartTime(),
                        eb.getEndTime()))
                {
                    return;
                }
                
                String time = String.valueOf(System.currentTimeMillis());
                // 发送推送消息和短信
                pushSmsService.pushSms(deviceId,
                        lon,
                        lat,
                        String.valueOf(encloId),
                        type,
                        gsm,
                        battery,
                        time);
                //再次查询，将写入数据库的信息读取出来
                eab = enclosuDAO.findAlarm(encloId);
                // 添加日志
                EnclosuLogBean enclosuLogBean = new EnclosuLogBean();
                enclosuLogBean.setEnclosuId(encloId);
                enclosuLogBean.setDeviceId(deviceId);
                enclosuLogBean.setAlarmInfo("越界告警");
                enclosuLogBean.setAlarmTimes(String.valueOf(eab.getAlarmTimes()));
                enclosuLogBean.setCreatTime(time);
                enclosuDAO.addAlarmLog(enclosuLogBean);
                Map<String, Object> encloMap = (Map<String, Object>) JacksonUtil.jsonToMap(alertInfo);
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
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
                multipleDataBean.setEnclosureId(encloId);
                enclosuDAO.addMultipleData(multipleDataBean);
                
                if (eab.getAlarmTimes() >= 3)
                {
                    logger.info("删除对应信息！");
                    encloService.delete(encloId);
                    logger.info("更新围栏信息暂停状态位！");
                    encloService.updateEncloInfo(encloId);
                    //清空缓存信息
                    logger.info("清空消息寄存器信息，键值为：" + encloId);
                    ElecAlarmConstant.elecAlarmMemoryMap.remove(String.valueOf(encloId));
                }
            }
            ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
        }
        catch (Exception e)
        {
            // 删除该围栏id的消息记录
            logger.error("定时告警错误", e);
            ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
            //encloService.delete(encloId);
        }
        
    }
    
    /**
     * <判断是否够条件触发告警>
     * <功能详细描述>
     * @param eab
     * @param cob1
     * @param encloId
     * @param atTime
     * @param nowTime
     * @return [能够触发返回true 不能够触发返回false]
     * 
     * @return boolean [返回类型说明] 能够触发返回true 不能够触发返回false
     * @throws Exception 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public boolean isAlam(EncloAlarmBean eab, CacheObjectBean cob1,
            int encloId, String atTime, long nowTime, String createTime,
            String startTime, String endTime) throws Exception
    {
        //数据点时间和围栏创建时间，如果数据点时间早于围栏创建时间，则返回false
        if (!StringTools.isEmptyOrNull(atTime))
        {
            Date date = DateTools.parse(atTime, "yyyy-MM-dd HH:mm:ss.s");
            if (!StringTools.isEmptyOrNull(createTime))
            {
                Date create_date = DateTools.parse(DateTools.format(createTime,
                        "yyyy-MM-dd HH:mm:ss.s"), "yyyy-MM-dd HH:mm:ss.s");
                if (DateTools.compareDate(create_date, date))
                {
                    logger.info("最新数据点的时间早于围栏创建时间，围栏ID:" + encloId);
                    return false;
                }
            }
            if (DateTools.compareHMD(startTime, endTime, date))
            {
                logger.info("最新数据点的时间早于围栏开始时间，围栏ID:" + encloId);
                return false;
            }
        }
        else
        {
            logger.info("获取数据点信息失败，围栏ID:" + encloId);
            return false;
        }
        
        if (!StringTools.isEmptyOrNull(eab))
        {
            if(eab.getAlarmTimes()>3)
            {
                logger.info("告警次数已经超过三次了!围栏Id为：" + encloId);
                return false;
            }
            //at为上一次告警数据点的时间
            String at = "";
            //如果围栏消息寄存器取不到本围栏的值，表示这是第一次触发告警,则不需要比较两次触发告警的数据点是否同一个
            if (!StringTools.isEmptyOrNull(cob1))
            {
                at = cob1.getLastDataTime();
                if (at.equals(atTime) && (eab.getAlarmTimes()+1) >= 2)
                {
                    
                    logger.info("最新数据点的时间和上一次告警的数据点时间一样!围栏Id为：" + encloId);
                    ElecAlarmConstant.threadMemoryMap.remove(String.valueOf(encloId));
                    return false;
                }
                
                cob1.setLastDataTime(atTime);
                cob1.setLastAlarmTime(nowTime);
                ElecAlarmConstant.elecAlarmMemoryMap.put(String.valueOf(encloId),
                        cob1);
            }
            else
            {
                CacheObjectBean cob2 = new CacheObjectBean();
                cob2.setEncloId(encloId);
                cob2.setLastDataTime(atTime);
                cob2.setLastAlarmTime(nowTime);
                ElecAlarmConstant.elecAlarmMemoryMap.put(String.valueOf(encloId),
                        cob2);
            }
            // 更新次数
            encloService.update(encloId, (eab.getAlarmTimes() + 1));
            logger.info("围栏ID为:" + eab.getEncloId() + "---更新次数:"
                    + (eab.getAlarmTimes() + 1));
        }
        else
        {
            //at为上一次告警数据点的时间
            String at = "";
            if (!StringTools.isEmptyOrNull(cob1))
            {
                at = cob1.getLastDataTime();
                logger.info(at);
                cob1.setLastDataTime(atTime);
                cob1.setLastAlarmTime(nowTime);
                ElecAlarmConstant.elecAlarmMemoryMap.put(String.valueOf(encloId), cob1);
            }
            else
            {
                CacheObjectBean cob2 = new CacheObjectBean();
                cob2.setEncloId(encloId);
                cob2.setLastDataTime(atTime);
                cob2.setLastAlarmTime(nowTime);
                ElecAlarmConstant.elecAlarmMemoryMap.put(String.valueOf(encloId),
                        cob2);
            }
            // 添加次数表
            EncloAlarmBean encloAlarmBean = new EncloAlarmBean();
            encloAlarmBean.setEncloId(encloId);
            encloAlarmBean.setAlarmTimes(1);
            enclosuDAO.addAlarm(encloAlarmBean);
            logger.info("告警次数添加完成：围栏ID : " + encloId);
        }
        return true;
    }
}
