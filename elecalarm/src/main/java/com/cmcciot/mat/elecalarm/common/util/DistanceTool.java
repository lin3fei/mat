package com.cmcciot.mat.elecalarm.common.util;

import java.text.DecimalFormat;

/**
 * 
 * 通过两个点的经纬度求这两点之间的距离
 * <功能详细描述>
 * 
 * @author fh
 * @version  [版本号, 2015年3月31日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DistanceTool {

	// 地球半径
	private static double EARTH_RADIUS = 6378.137;
	
	private static double rad(double d) {
	   return d * Math.PI / 180.0;
	}
	
	/**
     * 根据两点经纬度获得两点之间的距离
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * 
     * @return double 精确到米[参数说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
	public static double GetDistance(double lat1, double lon1, double lat2, double lon2) throws Exception {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2) +
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * EARTH_RADIUS;
		DecimalFormat df = new DecimalFormat("#.####");   
		return Math.round(Double.valueOf(df.format(s * 1000.0)));
	}
}
