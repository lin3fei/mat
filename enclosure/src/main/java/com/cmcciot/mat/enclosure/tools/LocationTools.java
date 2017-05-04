package com.cmcciot.mat.enclosure.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.enclosure.common.ClientErrorNumber;
import com.cmcciot.mat.enclosure.common.JacksonUtil;
import com.cmcciot.mat.enclosure.common.LogicException;

/**
 * 
 * <位置工具类>
 * <功能详细描述>
 * 
 * @author  
 * @version  [版本号, 2016年3月7日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class LocationTools 
{
    
    private static Logger logger = LoggerFactory.getLogger(LocationTools.class);
   
    
    public static String getGPSLocationByLBS(Integer mcc, Integer mnc, Integer lac,
            Integer cell)
    {
        String gpsLoc = null;
        Map<String, Object> gpsMap = new HashMap<String, Object>();
        try
        {
            
            gpsLoc = AccessLbsInterFaceTools.getGPSLocation(mcc, mnc, lac, cell);
            //获取到的经纬度保留小数点后5位
            gpsMap = JacksonUtil.jsonToMap(gpsLoc);
            String lat = String.valueOf(gpsMap.get("lat"));
            String lon = String.valueOf(gpsMap.get("lng"));
            if (!StringTools.isEmptyOrNull(lat)
                    && !StringTools.isEmptyOrNull(lon) && !lat.equals("null")
                    && !lon.equals("null"))
            {
                gpsMap.put("lat",
                        Double.valueOf(lat.substring(0,
                                lat.substring(lat.indexOf(".")).length() > 5 ? lat.indexOf(".") + 6
                                        : lat.length())));
                gpsMap.put("lng",
                        Double.valueOf(lon.substring(0,
                                lon.substring(lon.indexOf(".")).length() > 5 ? lon.indexOf(".") + 6
                                        : lon.length())));
            }
        }
        catch (Exception e)
        {
            logger.error("位置定位服务：位置转换错误,调用LBS接口错误！", e);
            if (e instanceof LogicException)
            {
                LogicException le = (LogicException) e;
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        le.getMessage());
            }
            else
            {
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        "服务器内部错误！");
            }
            
        }
        return JacksonUtil.mapToJson(gpsMap);
    }
    
    public static String getGPSLocationByMixLBS(Integer type,
            List<Map<String, Object>> celltowersList)
    {
        String gpsLoc = null;
        Map<String, Object> gpsMap = new HashMap<String, Object>();
        try
        {
            String celltowers = JacksonUtil.listToJson(celltowersList);
            celltowers = celltowers.replace("cid", "cell");
            
            //          StringBuffer celltowers = new StringBuffer("[");
            //          for(Map map:celltowersList){
            //              celltowers.append(JacksonUtil.mapToJson(map));
            //              celltowers.append(",");
            //          }
            //          celltowers.deleteCharAt(celltowers.length()-1);
            //          celltowers.append("]");
            
            gpsLoc = AccessLbsInterFaceTools.getGPSLocation(type, celltowers);
            gpsMap = JacksonUtil.jsonToMap(gpsLoc);
            String lat = String.valueOf(gpsMap.get("lat"));
            String lon = String.valueOf(gpsMap.get("lng"));
            if (!StringTools.isEmptyOrNull(lat)
                    && !StringTools.isEmptyOrNull(lon) && !lat.equals("null")
                    && !lon.equals("null"))
            {
                gpsMap.put("lat",
                        Double.valueOf(lat.substring(0,
                                lat.substring(lat.indexOf(".")).length() > 5 ? lat.indexOf(".") + 6
                                        : lat.length())));
                gpsMap.put("lng",
                        Double.valueOf(lon.substring(0,
                                lon.substring(lon.indexOf(".")).length() > 5 ? lon.indexOf(".") + 6
                                        : lon.length())));
            }
            
        }
        catch (Exception e)
        {
            logger.error("位置定位服务：位置转换错误,调用LBS接口错误！", e);
            if (e instanceof LogicException)
            {
                LogicException le = (LogicException) e;
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        le.getMessage());
            }
            else
            {
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        "服务器内部错误！");
            }
            
        }
        return JacksonUtil.mapToJson(gpsMap);
    }
    
    public static String getGPSLocationByGaode(Map<String, Object> map)
    {
        String gpsLoc = null;
        Map<String, Object> gpsMap = new HashMap<String, Object>();
        try
        {
            
            gpsLoc = AccessLbsInterFaceTools.getGPSLocationByGaode(map);
            //获取到的经纬度保留小数点后5位
            gpsMap = JacksonUtil.jsonToMap(gpsLoc);
            String lat = String.valueOf(gpsMap.get("lat"));
            String lon = String.valueOf(gpsMap.get("lng"));
            if (!StringTools.isEmptyOrNull(lat)
                    && !StringTools.isEmptyOrNull(lon) && !lat.equals("null")
                    && !lon.equals("null"))
            {
                gpsMap.put("lat",
                        Double.valueOf(lat.substring(0,
                                lat.substring(lat.indexOf(".")).length() > 5 ? lat.indexOf(".") + 6
                                        : lat.length())));
                gpsMap.put("lng",
                        Double.valueOf(lon.substring(0,
                                lon.substring(lon.indexOf(".")).length() > 5 ? lon.indexOf(".") + 6
                                        : lon.length())));
            }
        }
        catch (Exception e)
        {
            logger.error("位置定位服务：位置转换错误,调用LBS接口错误！", e);
            if (e instanceof LogicException)
            {
                LogicException le = (LogicException) e;
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        le.getMessage());
            }
            else
            {
                throw new LogicException(ClientErrorNumber.SERVICE_ERROR,
                        "服务器内部错误！");
            }
            
        }
        return JacksonUtil.mapToJson(gpsMap);
    }
}
