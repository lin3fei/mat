package com.cmcciot.mat.elecalarm.locationmanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.elecalarm.common.ServiceUserKeyConstant;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;
import com.cmcciot.mat.elecalarm.locationmanager.service.RespServiceMgr;

/**
 * 
 *云平台回调接口工具类
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年8月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class RespServiceMgrUtil
{
    static Logger logger = LoggerFactory.getLogger(ServiceMgrUtil.class);
    
    private static Properties props = new Properties();
    
    public static Map<String, String> methodMap = new HashMap<String, String>();
    
    public static Map<String, String> methodRespMap = new HashMap<String, String>();
    
    private static File file;
    static
    {
        file = new File(
                PropertyUtil.class.getResource("/respServiceMethod.properties")
                        .getPath());
        try
        {
            InputStream is = new FileInputStream(file);
            props.load(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        @SuppressWarnings("rawtypes")
        Enumeration enu = props.propertyNames();
        while (enu.hasMoreElements())
        {
            String key = enu.nextElement().toString();
            
            String targetValue = props.getProperty(key);
            
            String[] values = targetValue.split("\\|");
   
            methodMap.put(key, values[0]);
            
            methodRespMap.put(key, values[1]);
        }
    }
    
    
    public static String checkMessageBeforeHandOut(Map<String,Object> map){
        String respStr = "";
        String errno = "";
        String error = "";
        if (map.get("jsonError") != null)
        {
            errno = ServiceUserKeyConstant.ERRORCODE_FORMAT_ERROR.toString();
            
            error = "请求数据格式错误！";
            
            logger.error("服务管理：错误信息：请求数据格式错误！");
            
            respStr = JacksonUtil.putJson("",new String[]{"errno","error"},new String[]{errno,error});
        }else{
            
            String msgType = (String)map.get("msgType");
            
            if(!methodMap.containsKey(msgType)){
                
                errno = ServiceUserKeyConstant.ERRORCODE_FORMAT_ERROR.toString();
                
                error = "消息类型错误！";
                
                logger.error("服务管理：错误信息：无效的消息类型，消息类型："+msgType);
                
                respStr = JacksonUtil.putJson("",new String[]{"errno","error"},new String[]{errno,error});
                
            }
            
        }
        return respStr;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String handOut(RespServiceMgr respServiceMgr, Map<String, Object> map)
    {
        
        Class cls = respServiceMgr.getClass();
        Object value = "";
        String mapvalue = RespServiceMgrUtil.methodMap.get(map.get("msgType"));
        try
        {
            Method m = cls.getDeclaredMethod(mapvalue, Map.class);
            value = m.invoke(respServiceMgr, map);
        }
        catch (Exception e)
        {
            logger.error("服务管理：反射调用业务方法异常", e);
        }
        //暂时返回值只有string
        if (value.getClass() != String.class)
        {
            
            logger.error("服务管理：反射调用后，将内部接口：" + mapvalue + "返回值类型错误！");
            
        }
        logger.debug("服务管理调用业务接口后返回值：" + value);
        return (String) value;
    }
}
