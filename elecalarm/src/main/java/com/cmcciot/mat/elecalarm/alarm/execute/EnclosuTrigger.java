package com.cmcciot.mat.elecalarm.alarm.execute;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.cmcciot.mat.elecalarm.alarm.bean.CacheObjectBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EncloAlarmBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuBean;
import com.cmcciot.mat.elecalarm.alarm.dao.EnclosuDAO;
import com.cmcciot.mat.elecalarm.alarm.service.EncloService;
import com.cmcciot.mat.elecalarm.common.DataPointUtil;
import com.cmcciot.mat.elecalarm.common.ElecAlarmConstant;
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

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller("enclosuTrigger")
public class EnclosuTrigger
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
    private EncloService encloService;
    
    @Resource
    private PushSmsService pushSmsService;
    
    @SuppressWarnings("unchecked")
    public void execute()
    {
        try
        {
            logger.info("进入电子围栏业务");
            // 开始扫描所有添加电子围栏的设备
            List<EnclosuBean> deviceList = enclosuDAO.findDeviceList();
            if (StringTools.isEmptyOrNull(deviceList) || deviceList.size() == 0)
            {
                logger.info("没有添加电子围栏的设备！");
                return;
            }
            for (EnclosuBean eb : deviceList)
            {
                // 向设备云拉取数据
                String deviceId = String.valueOf(eb.getDeviceId());
                // 对设备星期的判断
                char[] week = eb.getWeekTime().toCharArray();
                logger.info("week "
                        + String.valueOf(week[DateTools.getWeekOfDate(new Date())]));
                if ("0".equals(String.valueOf(week[DateTools.getWeekOfDate(new Date())])))
                {
                    continue;
                }
                Date dateTime = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String createTime = eb.getCreateTime();
                // 对该设备进行时间判断 格式为10:00类型
                String startTime = eb.getStartTime();
                String endTime = eb.getEndTime();
                int n_duration = 0;
                int h_startTime = 0;
                int m_startTime = 0;
                int h_endTime = 0;
                int m_endTime = 0;
                h_startTime = Integer.valueOf(sdf.format(dateTime).substring(0,
                        2));//获取小时数（开始）
                h_endTime = Integer.valueOf(endTime.substring(0, 2));//获取小时数（结束）
                m_startTime = Integer.valueOf(sdf.format(dateTime).substring(3,
                        5));//获取分钟数（开始）
                m_endTime = Integer.valueOf(endTime.substring(3, 5));//获取分钟数（结束）
                if (m_endTime > m_startTime)
                {
                    
                    n_duration = (h_endTime - h_startTime) * 60
                            + (m_endTime - m_startTime);
                }
                else
                {
                    
                    n_duration = (h_endTime - h_startTime - 1) * 60
                            + (m_endTime + 60 - m_startTime);
                }
                //设置频率
                int freq = 60 * 15;
                //围栏的持续时间
                int duration = 60 * n_duration;
                //设置围栏持续时间
                int resetAs = 60 * 30;
                // 当前时间在此区间内继续执行，否则跳出当前执行
                if (DateTools.compareHM(startTime, endTime))
                {
                    continue;
                }
                else
                {
                    
                    //					pushSmsService.RegularReportingsettings(deviceId,
                    //							freq, duration, resetAs);
                }
                
                DeviceBean db = deviceDAO.findDeviceById(deviceId);
                if (StringTools.isEmptyOrNull(db))
                {
                    continue;
                }
                // sd设备ID
                String sdDeviceId = db.getSd_device_id();
                String jsonStr = DataPointUtil.getDataPoint(sdDeviceId);
                // 初始化经纬度
                double lat = 0.0;
                double lon = 0.0;
                
                // 初始化时间
                String time = "";
                
                //初始化频率
                String PerRpt = "";
                // 对返回json进行判断
                if (StringTools.isEmptyOrNull(jsonStr))
                {
                    continue;
                }
                else
                {
                    Map<String, Object> mapData = new HashMap<String, Object>();
                    mapData = JacksonUtil.jsonToMap(jsonStr);
                    
                    List<Map<String, Object>> datastreams = (List<Map<String, Object>>) mapData.get("datastreams");
                    if (StringTools.isEmptyOrNull(datastreams)
                            || datastreams.size() == 0)
                    {
                        continue;
                    }
                    for (Map<String, Object> datapoints : datastreams)
                    {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) datapoints.get("datapoints");
                        for (Map<String, Object> value : data)
                        {
                            Map<String, Object> valueData = (Map<String, Object>) value.get("value");
                            time = (String) value.get("at");
                            time.replace("T", " ");
                            PerRpt = (String) value.get("PerRpt");
                            String type = (String) valueData.get("type");
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
                    //判断其频率是否为1800，如果为1800，则，发送set短信
                    //					if(!StringTools.isEmptyOrNull(PerRpt)){
                    //						String[] rpt = PerRpt.split("\\|");
                    //						if(rpt[0].equals("1800")){
                    //
                    ////							pushSmsService.RegularReportingsettings(deviceId,
                    ////									freq, duration, resetAs);
                    //
                    //						}
                    //					}
                    
                    //判断围栏创建时间和最后数据点时间
                    // 最后个数据点做时间判断
                    if (!StringTools.isEmptyOrNull(time))
                    {
                        Date date = DateTools.parse(time,
                                "yyyy-MM-dd HH:mm:ss.s");
                        if (!StringTools.isEmptyOrNull(createTime))
                        {
                            Date create_date = DateTools.parse(DateTools.format(createTime,
                                    "yyyy-MM-dd HH:mm:ss.s"),
                                    "yyyy-MM-dd HH:mm:ss.s");
                            if (DateTools.compareDate(create_date, date))
                            {
                                continue;
                            }
                        }
                        if (DateTools.compareHMD(startTime, endTime, date))
                        {
                            continue;
                        }
                    }
                    else
                    {
                        continue;
                    }
                    
                    // 获取围栏设置好的圆心，半径
                    int enclosuRadius = eb.getEnclosuRadius();
                    Map<String, Object> centerMap = (Map<String, Object>) JacksonUtil.jsonToMap(eb.getEnclosuCenter());
                    // 通过经纬度获取设备到圆心的距离 并与半径作比较
                    if (StringTools.isEmptyOrNull((Double) centerMap.get("lat"))
                            || StringTools.isEmptyOrNull((Double) centerMap.get("lon")))
                    {
                        continue;
                    }
                    double radius = 0;
                    logger.info("通过开关判断圆心是否需要转高德经纬度~~");
                    if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGpsVal")))
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
                    // 设置的半径小于两点之间的距离，将产生报警
                    if ((radius - enclosuRadius) >= 0)
                    {
                        // 查找数据库中这个设备是否已经存在告警了
                        EncloAlarmBean eab = enclosuDAO.findAlarm(eb.getId());
                        if (!StringTools.isEmptyOrNull(eab))
                        {
                            continue;
                        }
                        else
                        {
                            EnclosuBean enclosuBean = enclosuDAO.findDevice(eb.getId());
                            // 再次扫描该设备
                            if (StringTools.isEmptyOrNull(enclosuBean))
                            {
                                continue;
                            }
                            // 将此告警添加到表中
                            EncloAlarmBean encloAlarmBean = new EncloAlarmBean();
                            encloAlarmBean.setEncloId(eb.getId());
                            encloAlarmBean.setAlarmTimes(1);
                            synchronized (encloAlarmBean)
                            {
                                enclosuDAO.addAlarm(encloAlarmBean);
                            }
                            logger.info("围栏告警已启动：围栏ID : " + eb.getId());
                            // 开启定时任务线程
                            encloService.encloResend(eb.getId(), time);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("电子围栏执行报错", e);
        }
    }
    
    public void executeNew()
    {
        
        try
        {
            logger.info("进入电子围栏业务");
            // 开始扫描所有添加电子围栏的设备
            List<EnclosuBean> deviceList = enclosuDAO.findDeviceList();
            if (StringTools.isEmptyOrNull(deviceList) || deviceList.size() == 0)
            {
                logger.info("没有电子围栏信息！");
                return;
            }
            for (EnclosuBean eb : deviceList)
            {
                /*获取设备ID*/
                String deviceId = String.valueOf(eb.getDeviceId());
                /*对围栏有效时间进行判断*/
                char[] week = eb.getWeekTime().toCharArray();
                /*获取今天围栏是否有效 0表示无效，1表示有效*/
                Date dateTime = new Date();
                
                String todayIsEffective = String.valueOf(week[DateTools.getWeekOfDate(dateTime)]);
                
                if ("0".equals(todayIsEffective))
                {
                    continue;
                }
                String startTime = eb.getStartTime();
                String endTime = eb.getEndTime();
                // 当前时间在此区间内继续执行，否则跳出当前执行
                if (DateTools.compareHM(startTime, endTime))
                {
                    continue;
                }
                /*执行到此表示这个围栏在有效期内，需要启动一个线程去判断该围栏能够触发*/
                //先到寄存器里面去查询，该围栏是否有正在触发的线程，没有就启动执行线程，有就不启动
                if (StringTools.isEmptyOrNull(ElecAlarmConstant.threadMemoryMap.get(eb.getId())))
                {
                    CacheObjectBean cob = new CacheObjectBean();
                    cob.setEncloId(eb.getId());
                    ElecAlarmConstant.threadMemoryMap.put(String.valueOf(eb.getId()), cob);
                    ElecAlarmConstant.PRODUCERPOOL.execute(new EnclosuExcuteThread(
                            eb.getId()));
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("电子围栏执行报错", e);
        }
        
    }
    
    public void update()
    {
        try
        {
            // 更新暂停状态
            logger.info("进入每日更新！");
            enclosuDAO.updateAllEncloInfo(0);
        }
        catch (Exception e)
        {
            logger.error("每日更新失败！", e);
        }
    }
}
