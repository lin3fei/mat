package com.cmcciot.mat.elecalarm.locationmanager.service;

import java.util.Map;

/**
 * 
 * <LBS转GPSservice>
 * <功能详细描述>
 * 
 * @author 傅豪
 * @version [版本号, 2015年1月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface LbsToGpsService {

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
    String getLbsToGps(Map<String, Object> map);
}
