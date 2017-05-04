package com.cmcciot.mat.elecalarm.locationmanager.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.apache.commons.codec.binary.Base64;

import com.cmcciot.mat.elecalarm.common.exception.ErrorNumber;
import com.cmcciot.mat.elecalarm.common.exception.LogicException;
import com.cmcciot.mat.elecalarm.common.util.StringTools;

/**
 * 
 * 访问LBS工具类
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年11月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class AccessLbsInterFaceTools
{
	
	/**
     * 混合基站信息转换成GPS信息
     * <混合定位转换>
     * @author 刘亮
     * @param celltowers
     * @return
     * @throws Exception [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getGPSLocation(Integer type, String celltowers)
    {
        
        String gpsLoc = null;
        
        String url = LbsPropertyTools.getValue("lbs.url");
        
        String requestUrl = LbsPropertyTools.getValue("lbs.requestMixUrl");
        
        String SICode = LbsPropertyTools.getValue("lbs.SICode");
        
        String secureKey = LbsPropertyTools.getValue("lbs.secureKey");
        
        StringBuffer params = new StringBuffer("?");
        
        params.append("type=" + type);
        
        params.append("&celltowers=" + celltowers);
        
        gpsLoc = request(url + requestUrl + params.toString(), requestUrl + params.toString(), SICode, secureKey, false);
        
        return gpsLoc;
        
    }
	
    /**
     * 基站信息转换成GPS信息
     * <功能详细描述>
     * @param mcc
     * @param mnc
     * @param lac
     * @param cell
     * @return
     * @throws Exception [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getGPSLocation(Integer mcc, Integer mnc, Integer lac,
            Integer cell)
    {
        
        String gpsLoc = null;
        
        String url = LbsPropertyTools.getValue("lbs.url");
        
        String requestUrl = LbsPropertyTools.getValue("lbs.requestUrl");
        
        String SICode = LbsPropertyTools.getValue("lbs.SICode");
        
        String secureKey = LbsPropertyTools.getValue("lbs.secureKey");
        
        StringBuffer params = new StringBuffer("?");
        
        /*stringToSign = Operate + "\n" +
                RequestUrl +"\n"+
                SecureKey + "\n" +
                Date*/
        
        //String signature = Base64Tools.encrypt(SHA1Tools.generateSHA1Str(stringToSign.toString()));
        
        //mcc=?&mnc=?&lac=?&cell=?
        params.append("mcc=" + mcc);
        
        params.append("&mnc=" + mnc);
        
        params.append("&lac=" + lac);
        
        params.append("&cell=" + cell);
        
        gpsLoc = request(url + requestUrl + params.toString(), requestUrl + params.toString(), SICode, secureKey, false);
        
        return gpsLoc;
        
    }
    
    private static String request(String fullUrl, String requestUrl,String sicode,String key,boolean logger) {
        try {
            
            String date = Calendar.getInstance().getTime().toString();
            String signature = sign("GET", requestUrl, date, key);
            StringBuffer authorization = new StringBuffer();
            authorization.append(sicode).append(":").append(signature);

            
            long starttime = System.currentTimeMillis();
            
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("Auth-Date", date);
            connection.setRequestProperty("Authorization",authorization.toString());
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            StringBuffer resp = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                resp.append(line);
                resp.append("\r\n");
            }
            reader.close();
            connection.disconnect();
            
            long endtime = System.currentTimeMillis();
            
            
            if(logger){
                System.out.println("GET "+ url +" HTTP/1.1");
                System.out.println("Date: " +date);
                System.out.println("Authorization: " +authorization);
                System.out.println("#####################");
                System.out.println("Take " +(endtime-starttime) +"ms\r\n" + resp);
            }
            
            return resp.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String sign(String operation, String requestUrl, String date,String key) {
        String signature = null;
        StringBuffer buf = new StringBuffer();
        if (isNotBlank(operation)) {
            buf.append(operation).append("\n");
        } else {
            buf.append("\n");
        }
        if (isNotBlank(requestUrl)) {
            buf.append(requestUrl).append("\n");
        } else {
            buf.append("\n");
        }
        if (isNotBlank(key)) {
            buf.append(key).append("\n");
        } else {
            buf.append("\n");
        }
        if (isNotBlank(date)) {
            buf.append(date);
        } else {
            buf.append("\n");
        }
        String data = buf.toString();
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            byte[] keyBytes = key.getBytes("UTF8");
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            mac.init(signingKey);
            byte[] signBytes = mac.doFinal(data.getBytes("UTF8"));
            signature = Base64.encodeBase64String(signBytes);
            return signature;
        } catch (Exception e) {
            throw new RuntimeException("MAC CALC FAILED.");
        }
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
	public static String getGPSLocationByGaode(Map<String, Object> map) {

		try {
    		String jsonStr = "";
    		String url = LbsPropertyTools.getValue("lbs.url");
            
            String requestUrl = LbsPropertyTools.getValue("lbs.requestGaodeUrl");
            
            String SICode = LbsPropertyTools.getValue("lbs.SICode");
            
            String secureKey = LbsPropertyTools.getValue("lbs.secureKey");
            
            String gaodeKey = LbsPropertyTools.getValue("lbs.gaodeKey");
            
	    	// 通过传参拼接参数
	    	StringBuffer urlParamSb = new StringBuffer();
	    	String IP = "";
	    	if(StringTools.isEmptyOrNull((String) map.get("serverip"))){
	    	}else{
	    		IP = (String) map.get("serverip");
	    	}
	    	
	    	
	    	String IMSI =(String) map.get("IMSI");
	    	String IMEI =	(String) map.get("IMEI");
	    	List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LBS");
	    	Map<String, Object> tempMap = new HashMap<String, Object>();
	    	StringBuffer nearbtsBuffer = new StringBuffer();
	    	for(int i=0;i<list.size();i++){
				   nearbtsBuffer.append(list.get(i).get("mcc")+",")
					 	    	  .append(list.get(i).get("mnc")+",")
						    	  .append(list.get(i).get("lac")+",")
						    	  .append(list.get(i).get("cid")+",");
					if(list.size() -1 == i){
						 nearbtsBuffer.append(list.get(i).get("ss"));
					}else{
						 nearbtsBuffer.append(list.get(i).get("ss")+"|");	
					}
			}
	    	tempMap = (Map<String, Object>) list.get(0);
	    	StringBuffer btsBuffer = new StringBuffer();
	    	btsBuffer.append(tempMap.get("mcc")+",")
			    	 .append(tempMap.get("mnc")+",")
			    	 .append(tempMap.get("lac")+",")
			    	 .append(tempMap.get("cid")+",")
			    	 .append(tempMap.get("ss"));
	    	
	    	urlParamSb.append("?").append("accesstype=0")
			    	.append("&imei=" + IMEI)
			    	.append("&imsi=" + IMSI)
			    	.append("&smac=" + URLEncoder.encode("E0:DB:55:E4:C7:49"))
			    	.append("&cdma=0")
			    	.append("&bts="+URLEncoder.encode(btsBuffer.toString()))    				     
			    	.append("&nearbts="+URLEncoder.encode(nearbtsBuffer.toString()))
			    	.append("&serverip=" + IP)
			    	.append("&output=json").append("&key=" + gaodeKey);
			jsonStr = request(url + requestUrl + urlParamSb.toString(), requestUrl + urlParamSb.toString(),
					SICode, secureKey);
	    	return jsonStr;
    	} catch (Exception e) {
    		e.printStackTrace();
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "调用高德地图 接口异常(~call gaode MAP interface failed!)");
    	}
	
    }

    private static String request(String fullUrl, String requestUrl, String sicode, String key) {
        try {
            
            String date = Calendar.getInstance().getTime().toString();
            String signature = sign("GET", requestUrl, date, key);
            StringBuffer authorization = new StringBuffer();
            authorization.append(sicode).append(":").append(signature);
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Auth-Date", date);
            connection.setRequestProperty("Authorization",authorization.toString());
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            StringBuffer resp = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                resp.append(line);
                resp.append("\r\n");
            }
            reader.close();
            connection.disconnect();
            
            return resp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private static boolean isNotBlank(String value){
        return (value!=null && value.length()>0);
    }
}
