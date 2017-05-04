package com.cmcciot.mat.elecalarm.locationmanager.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * 
 * <位置定位service>
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年11月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface LocationService
{
    /**
     * 通过LBS位置信息得到GPS位置信息
     * <功能详细描述>
     * @param mcc
     * @param mnc
     * @param lac
     * @param cell
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    String getGPSLocationByLBS(Integer mcc,Integer mnc,Integer lac,Integer cell);
    
    /**
     * 通过混合LBS位置信息得到GPS位置信息
     * <功能详细描述>
     * @author 刘亮
     * @param type(0:gps坐标,1:google坐标 ,2:百度坐标)
     * @param celltowers(List集合，其中每个元素类型为Map，每个Map包含4个entry（4个key分别是"mcc","mnc","lac","cell",value为数字字符串))
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    String getGPSLocationByMixLBS(Integer type, List<Map<String,Object>> celltowersList);
    
    /**
     * 通过混合LBS位置信息得到高德地图GPS位置信息
     * <功能详细描述>
     * @param map
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    String getGPSLocationByGaode(Map<String, Object> map);
    
    
    /**
     * 把本地GPS转换成高德地图GPS
     * @parameter map
     * @return  [json]
     * */
    
    public JSONObject localGpsToGaodeGps(Map<String, Object> map);
}
