/**  
* 2015-8-27  
* author:zoujiang
*/  

package com.cmcciot.mat.enclosure.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.shiro.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.enclosure.business.userManager.service.EnClosuerService;
import com.cmcciot.mat.enclosure.common.JacksonUtil;
import com.cmcciot.mat.enclosure.model.domain.AlertMessageBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosuLogBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosureDeviceHistorydatastreamBean;
import com.cmcciot.mat.enclosure.model.domain.MultipleDataBean;
import com.cmcciot.mat.enclosure.tools.AccessLbsInterFaceTools;
import com.cmcciot.mat.enclosure.tools.LbsPropertyTools;
import com.cmcciot.mat.enclosure.tools.LocationTools;
import com.cmcciot.mat.enclosure.tools.PropertyUtil;
import com.cmcciot.mat.enclosure.tools.StringTools;

public class EnclosureThread extends Thread {

	// 地球半径
	private static double EARTH_RADIUS = 6378.137;
	private static Logger logger = LoggerFactory.getLogger(EnclosureThread.class);
    private String jsonStr;
	private Map<String, Object> bean;
	private EnClosuerService enClosuerService;
	private static Pattern phonePattern = Pattern.compile("^[\\d]{11}|[\\d]{13}$");

	
	
	
	public EnclosureThread(String json,Map<String, Object> map, EnClosuerService service){
		this.jsonStr = json;
		this.bean = map ;
		this.enClosuerService = service;
	}
	
	@Override
	public void run() {
		
		dealData(jsonStr, bean);
	}
	
	
	@SuppressWarnings("unchecked")
    private void dealData(String jsonStr, Map<String, Object> bean){
		
		logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+"获取设备云数据点数据："+jsonStr);
		Object longitude = bean.get("LONGITUDE");
		Object historyId = bean.get("HISTORYDATAID");
		Object monitor_name = bean.get("MONITOR_NAME"); //被监控人名字
		Object leader_phone = bean.get("IORG_LEADER_PHONE"); //部门管理员手机号码
		Object enclosuid = bean.get("ENCLOSUID"); //围栏ID
		Object center = bean.get("ENCLOSU_CENTER");
		Object center_radius = bean.get("ENCLOSU_RADIUS");
		Object updatedate = bean.get("UPDATEDATE");
		JSONObject centerJson = JSONObject.fromObject(center);
		double centerLat = centerJson.optDouble("lat");
		double centerLon = centerJson.optDouble("lon");
		Object alarmDate = bean.get("ALARMDATE");
		Object alarmType = bean.get("ENCLOSU_TYPE");  //告警类型： 1.进圈  2.出圈
		Object IMEI = bean.get("DEVICE_IMEI");
		Object IMSI = bean.get("DEVICE_IMSI");
        JSONObject data = JSONObject.fromObject(jsonStr);
        JSONArray datastreams =  data.optJSONArray("datastreams")  ;
        if (StringTools.isEmptyOrNull(datastreams) || datastreams.size() == 0)
        {
           return;
        }
        float lat = 0f, lon = 0f;
        //取最后一条数据的最后一个点
    	JSONObject datapoints = datastreams.getJSONObject(datastreams.size()-1);
    	JSONArray points = datapoints.getJSONArray("datapoints");
		JSONObject valueData = points.getJSONObject(points.size()-1).getJSONObject("value");
		//时间点
		String at = points.getJSONObject(points.size()-1).get("at").toString().substring(0,19);
		String type =  valueData.optString("type");
		if ("GPS".equals(type))
        {
            lat = Float.parseFloat(valueData.get("lat").toString());
            lon = Float.parseFloat(valueData.get("lon").toString());
        }else if ("LBS".equals(type)){
            
            

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("IMEI", IMEI);
            paramMap.put("IMSI", IMSI);
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
                    gpsStr = LocationTools.getGPSLocationByGaode(paramMap);
                }
                else
                {
                    gpsStr = LocationTools.getGPSLocationByMixLBS(0,
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
                    gpsStr = LocationTools.getGPSLocationByGaode(paramMap);
                }
                else
                {
                    gpsStr = LocationTools.getGPSLocationByLBS(Integer.valueOf(mcc),
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
                lat = Float.parseFloat(gpsMap.get("gaode_lat").toString());
                lon = Float.parseFloat(gpsMap.get("gaode_lng").toString());
            }
            else
            {
                lat = Float.parseFloat( gpsMap.get("lat").toString());
                lon = Float.parseFloat( gpsMap.get("lng").toString());
            }
        
        }
		//将gps转换成百度经纬度
		try {
			logger.info("EnclosureJob:设备："+bean.get("DEVICEID") +" 从设备云得到传到百度的经纬度：LON="+lon+",LAT="+lat);
			JSONObject json = GetBaiduLocation(lon, lat);
			 logger.info("EnclosureJob:设备："+bean.get("DEVICEID") +" 从设备云得到并且转换成百度经纬度之后的串："+json);
			if(json != null){
				if("0".equals(json.optString("error"))){
					lon =  (float) (ConvertBase64(json.getString("x")));
					lat =  (float) (ConvertBase64(json.getString("y")));
					
				}else{
					lon = 0f;
					lat = 0f;
				}
			}
		} catch (MalformedURLException e) {
			logger.error("告警线程报错",e);
		} catch (IOException e) {
		    logger.error("告警线程报错",e);
		}
		
       // 判断历史数据里面是否有记录， 即longitude是否为空， 如果空则新增，跳过以下的跨围栏验证
        //      如果非空， 则更新， 并验证是否跨围栏
        Map<String, Object> params  = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("EnclosureJob:设备："+bean.get("DEVICEID") +" 从设备云得到并且转换成百度经纬度之后的值 LON="+lon+",LAT="+lat);
        if(lon > 0 && lat >0){
            EnclosureDeviceHistorydatastreamBean enBean =new EnclosureDeviceHistorydatastreamBean();
            enBean.setDeviceid(String.valueOf(bean.get("DEVICEID")));
            enBean.setEnclosuid(String.valueOf(bean.get("ENCLOSUID")));
            enBean.setLatitude(String.valueOf(lat));
            enBean.setLongitude(String.valueOf(lon));
            enBean.setUpdatedate(at);
        	
        	params.put("deviceid", bean.get("DEVICEID"));
        	params.put("enclosuid", bean.get("ENCLOSUID"));
        	params.put("longitude", lon);
        	params.put("latitude", lat);
        	params.put("updatedate", at);
        	if(longitude == null || "".equals(longitude)){
        	    

                //新获取的数据点离圆心的距离
                double radius2 = GetDistance(centerLat,centerLon, lat,  lon);
                logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 距离圆心距离 "+radius2 +"米。");
                if("1".equals(alarmType.toString())){
                    //进圈
                    if(Double.parseDouble(center_radius.toString()) > radius2){
                        logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 进入围栏，发送告警！ ");
                        params.put("alarmdate", sdf.format(new Date()));
                        enBean.setAlarmdate(sdf.format(new Date()));
                        sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid, valueData,monitor_name.toString(),leader_phone.toString());
                    }
                }else if("2".equals(alarmType.toString())){
                    //出圈
                    if(Double.parseDouble(center_radius.toString()) < radius2){
                        logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 超出围栏，发送告警！ ");
                        params.put("alarmdate", sdf.format(new Date()));
                        enBean.setAlarmdate(sdf.format(new Date()));
                        sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid,valueData,monitor_name.toString(),leader_phone.toString());
                    }
                }
           
            
        	    logger.info("longitude为空");
        	    
        	    enClosuerService.addEnClosureDeviceHistory(enBean);
        	}else{
        		//还是上次一次得到的点数据一样， 不告警
        		//if(Float.parseFloat(longitude.toString()) == lon && Float.parseFloat(latitude.toString())  == lat ){
        		//如果上次得到数据的时间和本次一样，则不告警
        		if(at.equals(updatedate.toString()) ){
        			
        			logger.info("EnclosureJob:设备："+bean.get("DEVICEID") +" 获取到的数据点时间和数据库中存的数据点时间一致，数据无变化， 不告警！");
        		}else if(alarmDate != null && !"".equals(alarmDate)){
        			//判断上次告警时间距离现在是否超过15分钟， 如果未超过，无论是否超出围栏， 都不告警
        			if(compairTime(alarmDate.toString()) >= 15){
        				
						 //新获取的数据点离圆心的距离
						 double radius2 = GetDistance(centerLat,centerLon, lat,  lon);
						 logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 距离圆心距离 "+radius2 +"米。");
						 if("1".equals(alarmType.toString())){
							 //进圈
							 if(Double.parseDouble(center_radius.toString()) > radius2){
								 logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 进入围栏，发送告警！ ");
								 params.put("alarmdate", sdf.format(new Date()));
								 sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid, valueData,monitor_name.toString(),leader_phone.toString());
							 }
						 }else if("2".equals(alarmType.toString())){
							 //出圈
							 if(Double.parseDouble(center_radius.toString()) < radius2){
								 logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 超出围栏，发送告警！ ");
								 params.put("alarmdate", sdf.format(new Date()));
								 sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid,valueData,monitor_name.toString(),leader_phone.toString());
							 }
						 }
        			}else{
        				logger.info("EnclosureJob:设备："+bean.get("DEVICEID") +" 距离上次告警时间小于15分钟，不告警！");
        			}
        		}else {
                    //新获取的数据点离圆心的距离
                    double radius2 = GetDistance(centerLat,centerLon, lat,  lon);
                    logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 距离圆心距离 "+radius2 +"米。");
                    if("1".equals(alarmType.toString())){
                        //进圈
                        if(Double.parseDouble(center_radius.toString()) > radius2){
                            logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 进入围栏，发送告警！ ");
                            params.put("alarmdate", sdf.format(new Date()));
                            sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid, valueData,monitor_name.toString(),leader_phone.toString());
                        }
                    }else if("2".equals(alarmType.toString())){
                        //出圈
                        if(Double.parseDouble(center_radius.toString()) < radius2){
                            logger.info("EnclosureJob:设备："+bean.get("DEVICEID")+" 超出围栏，发送告警！ ");
                            params.put("alarmdate", sdf.format(new Date()));
                            sendAlramMessage(bean.get("DEVICEID").toString(), lon, lat, (int)enclosuid,valueData,monitor_name.toString(),leader_phone.toString());
                        }
                    }
               
                }
        		//新获取到的数据点存入数据库
        		params.put("historyId", historyId.toString());
        		enClosuerService.updateEnClosureDeviceHistoryDataStrams(params);
        	}
        }
        
	}
	
	public void  sendAlramMessage(String deviceId,double lon,double lat,int  encloId,  JSONObject valueData, String monitor_name, String leader_phone){
		
	        List<Map<String, Object>> list= enClosuerService.selectBeviceid(deviceId);
	        Map<String, Object> map= list.get(0);
	        String id = map.get("ID").toString();
            
            String time = String.valueOf(System.currentTimeMillis());
            // 发送推送消息和短信
            pushSms( monitor_name,leader_phone);
            // 添加日志
            EnclosuLogBean enclosuLogBean = new EnclosuLogBean();
            enclosuLogBean.setEnclosuId(encloId);
            enclosuLogBean.setDeviceId(deviceId);
            enclosuLogBean.setAlarmInfo("越界告警");
            enclosuLogBean.setAlarmTimes("1");
            enclosuLogBean.setCreatTime(time);
            enClosuerService.addAlarmLog(enclosuLogBean);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeTemp = format.format(Long.valueOf(time));
            valueData.put("alertType", "1");
            valueData.remove("LBSDATAS");
            valueData.put("lon", lon);
            valueData.put("lat", lat);
            valueData.put("time", timeTemp);
            // 添加日志到告警信息表
            AlertMessageBean amb = new AlertMessageBean();
//            amb.setAlertDeviceId(deviceId);
            amb.setAlertDeviceId(id);
            amb.setAlertTime(time);
            amb.setSaveTime(time);
            amb.setAlertIndex("1");
            amb.setAlertInfo(valueData.toString());
            amb.setEnclosureId(encloId);
            enClosuerService.addAlertMessage(amb);
            // 将上报通知存入综合数据表
            MultipleDataBean multipleDataBean = new MultipleDataBean();
            multipleDataBean.setDeviceId(deviceId);
            multipleDataBean.setType("alarm");
            multipleDataBean.setTime(time);
            multipleDataBean.setData(valueData.toString());
            multipleDataBean.setEnclosureId(encloId);
            enClosuerService.addMultipleData(multipleDataBean);
		
	}
	
	
	/**
	 * 输入时间和当前时间的时间差， 返回分钟
	 * */
	 public static int compairTime(String t1){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  try {
			return (int) ((System.currentTimeMillis()- sdf.parse(t1).getTime()) /1000 /60) ;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 return 0;
	 }
	
	 public String getGPSLocationByMixLBS(Integer type,List<Map<String, Object>> celltowersList)
	    {
	        String gpsLoc = null;
	        JSONObject gpsMap = null;
	        try
	        {
	            String celltowers =JSONSerializer.toJSON(celltowersList).toString();
	            celltowers = celltowers.replace("cid", "cell");
	            gpsLoc = AccessLbsInterFaceTools.getGPSLocation(type, celltowers);
	            gpsMap = JSONObject.fromObject(gpsLoc) ;
	            String lat = String.valueOf(gpsMap.get("lat"));
	            String lon = String.valueOf(gpsMap.get("lng"));
	            if (!StringTools.isEmptyOrNull(lat)
	                    && !StringTools.isEmptyOrNull(lon) && !lat.equals("null")
	                    && !lon.equals("null"))
	            {
	                gpsMap.put("lat", Double.valueOf(lat.substring(0,
	                                lat.substring(lat.indexOf(".")).length() > 5 ? lat.indexOf(".") + 6
	                                        : lat.length())));
	                gpsMap.put("lng",Double.valueOf(lon.substring(0,
	                                lon.substring(lon.indexOf(".")).length() > 5 ? lon.indexOf(".") + 6
	                                        : lon.length())));
	            }
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            
	        }
	        return gpsMap.toString();
	    }
	 
	 public String getGPSLocationByLBS(Integer mcc, Integer mnc, Integer lac,Integer cell)
	    {
	        String gpsLoc = null;
	        JSONObject gpsMap = new JSONObject();
	        try
	        {
	            
	            gpsLoc = AccessLbsInterFaceTools.getGPSLocation(mcc, mnc, lac, cell);
	            //获取到的经纬度保留小数点后5位
	            gpsMap = JSONObject.fromObject(gpsLoc);
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
	            e.printStackTrace();
	            
	        }
	        return gpsMap.toString();
	    }
	 
		 
		 
		public static JSONObject GetBaiduLocation(float x, float y) throws MalformedURLException, IOException {
		    String url = String.format("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=%f&y=%f", x, y);
		    HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
		    urlConnection.connect();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		    String lines = reader.readLine();
		    reader.close(); 
		    urlConnection.disconnect();
		    JSONObject json = null;
		    if(lines.startsWith("{") && lines.endsWith("}")) {
	        	 json = JSONObject.fromObject(lines);
	        }
		    return json;
		}   
		 
		private static float ConvertBase64(String str) {
		    byte[] bs = Base64.decode(str);       
		    return Float.valueOf(new String(bs));
		}
		
		
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
		public static double GetDistance(double lat1, double lon1, double lat2, double lon2) {
			
			try {
				double radLat1 = rad(lat1);
				double radLat2 = rad(lat2);
				double a = radLat1 - radLat2;
				double b = rad(lon1) - rad(lon2);

				double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2) +
						Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
				s = s * EARTH_RADIUS;
				DecimalFormat df = new DecimalFormat("#.####");   
				return Math.round(Double.valueOf(df.format(s * 1000.0)));
			} catch (NumberFormatException e) {
				
				return 0;
			}
		}
		/**
		 * 发送短信
		 * */
		public void pushSms( String monitor_name, String leader_phone) {
            try {
                String httpSite = PropertyUtil.getValue("httpPlatform");
                String url = httpSite + "/sendSms?phone=" + leader_phone + "&monitor_name=" + URLEncoder.encode(monitor_name,"UTF-8");
                logger.info("调用platform的发送短信接口：URL" + url);
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                client.execute(get);
            } catch (Exception e) {
                logger.error("调用推送短信失败", e);
                try {
                    throw new Exception("调用推送短信失败");
                } catch (Exception e1) {
                    logger.error("调用推送短信失败", e1);
                }
            }
		}
	    /**
	     * 检验电话号码的合法性
	     * <功能详细描述>
	     * @param phoneNo
	     * @return [参数说明]
	     * 
	     * @return boolean [返回类型说明]
	     * @exception throws [违例类型] [违例说明]
	     * @see [类、类#方法、类#成员]
	     */
	    public static boolean validatePhones(String phoneNo)
	    {
	        boolean flag = false;
	        
	        if (phonePattern.matcher(phoneNo).matches())
	        {
	            flag = true;
	        }
	        return flag;
	    }
	    
}
