package com.cmcciot.mat.elecalarm.locationmanager.service.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.common.exception.ClientErrorNumber;
import com.cmcciot.mat.elecalarm.common.exception.LogicException;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.locationmanager.bean.LatLng;
import com.cmcciot.mat.elecalarm.locationmanager.bean.PointM;
import com.cmcciot.mat.elecalarm.locationmanager.dao.LocationDAO;
import com.cmcciot.mat.elecalarm.locationmanager.service.LocationService;
import com.cmcciot.mat.elecalarm.locationmanager.util.AccessLbsInterFaceTools;
import com.cmcciot.mat.elecalarm.locationmanager.util.MapUtils;

/**
 * 
 * <位置定位service实现>
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年11月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service("locationService")
public class LocationServiceImpl implements LocationService
{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private LocationDAO locationDao;
    
    @Override
    public String getGPSLocationByLBS(Integer mcc, Integer mnc, Integer lac,
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
    
    /**
     * @author 刘亮
     */
    @Override
    public String getGPSLocationByMixLBS(Integer type,
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
    
    @Override
    public String getGPSLocationByGaode(Map<String, Object> map)
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
    
    public JSONObject localGpsToGaodeGps(Map<String, Object> map)
    {
        Object loc_lat = map.get("lat");
        Object loc_lng = map.get("lng");
        try
        {
            if (loc_lat != null && loc_lng != null)
            {
                double lat = Double.parseDouble(loc_lat.toString());
                double lng = Double.parseDouble(loc_lng.toString());
                //第二步：将你的经纬度转成像素点
                PointM point = MapUtils.formLatLngToPixel(new LatLng(lat, lng),
                        18);
                //第三步：根据你的经纬度查询出偏移像素；24.91 和 102.13 ；保留两位小数后乘以100得到整数
                //select offsetX,offsetY from dbo.google where Lng=10213 and Lat = 2491
                DecimalFormat df = new DecimalFormat("######0.00");
                
                int i_lat = (int) (Double.parseDouble(df.format(lat)) * 100);
                int i_lng = (int) (Double.parseDouble(df.format(lng)) * 100);
                PointM queryP = new PointM(i_lat, i_lng);
                PointM offset = locationDao.queryOffset(queryP);
                logger.info("查询到的   Offsetlat: " + offset.getOffsetlat()
                        + "   Offsetlng:" + offset.getOffsetlng());
                point.setx(Math.round(point.getx() + offset.getOffsetlat()));
                point.sety(Math.round(point.gety() + offset.getOffsetlng()));
                //第四步：根据像素点获得经纬度。
                LatLng model = MapUtils.fromPixelToLatLng(point, 18);
                //这个model就是转换后的经纬度对象。外号：火星经纬度。
                DecimalFormat dft = new DecimalFormat("######.00000");
                double gaode_lat = Double.parseDouble(dft.format(model.getLat()));
                double gaode_lng = Double.parseDouble(dft.format(model.getLng()));
                model = new LatLng(gaode_lat, gaode_lng);
                System.out.println(model.getLat() + "," + model.getLng());
                logger.info("本地GPS转高德GPS成功!转换之前GPS:LAT=" + lat + ",LNG=" + lng
                        + ";转换之后GPS:LAT=" + model.getLat() + ",LNG="
                        + model.getLng());
                return JSONObject.fromObject(model);
            }
        }
        catch (Exception e)
        {
            
            logger.info("本地GPS转高德GPS时异常：" + e.getMessage());
        }
        
        return null;
    }
}
