package com.cmcciot.mat.elecalarm.locationmanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.common.exception.LogicException;
import com.cmcciot.mat.elecalarm.common.exception.TerminalError;
import com.cmcciot.mat.elecalarm.common.exception.TerminalErrorNumber;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.locationmanager.service.LbsToGpsService;
import com.cmcciot.mat.elecalarm.locationmanager.service.LocationService;
import com.cmcciot.mat.elecalarm.locationmanager.util.LbsPropertyTools;
import com.cmcciot.mat.elecalarm.locationmanager.util.RespServiceMgrUtil;

/**
 * 
 * <LBS转GPSservice实现>
 * <功能详细描述>
 * 
 * @author 傅豪
 * @version [版本号, 2015年1月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Service("lbsToGpsService")
public class LbsToGpsServiceImpl implements LbsToGpsService
{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private LocationService locationService;
    
    @SuppressWarnings("unchecked")
    public String getLbsToGps(Map<String, Object> map)
    {
        int errno;
        
        String error = "";
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        String jsonStr = "";
        
        try
        {
            // 设备IMEI
            String imei = (String) map.get("IMEI");
            // 设备序列号
            String sn = (String) map.get("SN");
            // 设备自行维护，自增即可，服务侧将原样返回
            String serial = (String) map.get("serial");
            // LBS转GPS 返回消息类型
            String msgType = RespServiceMgrUtil.methodRespMap.get(map.get("msgType"));
            // LBS时使用参数
            String mcc = (String) map.get("mcc");
            // LBS时使用参数
            String mnc = (String) map.get("mnc");
            // LBS时使用参数
            String lac = (String) map.get("lac");
            // LBS时使用参数
            String cid = (String) map.get("cid");
            // 请求消息类型
            String type = (String) map.get("type");
            
            // 如果请求消息类型不是LBS，或者请求参数为空返回错误
            if (StringTools.isEmptyOrNull(type) || !"LBS".equals(type)
                    || StringTools.isEmptyOrNull(imei)
                    || StringTools.isEmptyOrNull(sn)
                    || StringTools.isEmptyOrNull(serial))
            {
                resultMap.put("msgType", msgType);
                resultMap.put("serial", serial);
                resultMap.put("errno", TerminalErrorNumber.CODE_FAIL);
                resultMap.put("error", "type类型错误或者请求参数不合法");
                jsonStr = JacksonUtil.mapToJson(resultMap);
                return jsonStr;
            }
            List<Map<String, Object>> celltowersList = (List<Map<String, Object>>) map.get("LBSDATAS");
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("IMEI", imei);
            paramMap.put("IMSI", null);
            paramMap.put("serverip", null);
            String gpsStr = "";
            // 转换LBS到GPS
            if (!StringTools.isEmptyOrNull(celltowersList)
                    && celltowersList.size() > 0)
            {
            	if ("on".equals(LbsPropertyTools.getValue("lbs.gaodeGps"))) {
            		paramMap.put("LBS", celltowersList);
                    gpsStr = locationService.getGPSLocationByGaode(paramMap);
            	} else {
            		gpsStr = locationService.getGPSLocationByMixLBS(0, celltowersList);
            	}
            }
            else
            {
                if (StringTools.isEmptyOrNull(mcc)
                        || StringTools.isEmptyOrNull(mnc)
                        || StringTools.isEmptyOrNull(lac)
                        || StringTools.isEmptyOrNull(cid))
                {
                    resultMap.put("msgType", msgType);
                    resultMap.put("serial", serial);
                    resultMap.put("errno", TerminalErrorNumber.CODE_FAIL);
                    resultMap.put("error", "parameter can't be null");
                    jsonStr = JacksonUtil.mapToJson(resultMap);
                    return jsonStr;
                }
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
            
            // 生成返回json信息
            resultMap.put("msgType", msgType);
            resultMap.put("serial", serial);
            resultMap.put("errno", TerminalErrorNumber.CODE_SUCCESS);
            resultMap.put("error", TerminalError.SUCCESS);
            // 经度
            resultMap.put("lon", gpsMap.get("lng"));
            // 纬度
            resultMap.put("lat", gpsMap.get("lat"));
            resultMap.put("type", "GPS");
            
            jsonStr = JacksonUtil.mapToJson(resultMap);
            
            return jsonStr;
        }
        catch (Exception e)
        {
            logger.error("服务管理：LBS转GPS错误！", e);
            errno = TerminalErrorNumber.CODE_FAIL;
            error = TerminalError.SERVER_ERROR;
            throw new LogicException(errno, error);
        }
    }
}
