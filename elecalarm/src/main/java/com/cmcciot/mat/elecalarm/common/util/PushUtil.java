package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * <调用PushApi平台下推送信息工具类>
 * <功能详细描述>
 * 
 * @author  hth
 * @version  [版本号, 2015年3月23日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class PushUtil
{
    private static Log logger = LogFactory.getLog(PushUtil.class);
    
    private static String agentUrl = CfgPropertyTool.getValue("agentUrl");
    
    private static String userId = CfgPropertyTool.getValue("userId");
    
    private static String smsAuthKey = CfgPropertyTool.getValue("smsAuthKey");
    
    //推送成功返回码
    private static String resultSuccessCode = "0";
    
    //请求命令
    private static String cmd = "pushMsg";
    
    private static final String APPLICATION_JSON = "application/json";
    
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    
    /**
     * <调用PushApi平台下推送信息方法>
     * <功能详细描述>
     * @param phoneNo
     * @param content
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean pushSmsParam(Map<String, Object> map)
    {
        boolean flag = false;
        
        String params = "";
        //判断配置项
        if (StringTools.isEmptyOrNull(agentUrl)
                || StringTools.isEmptyOrNull(userId)
                || StringTools.isEmptyOrNull(smsAuthKey))
        {
            logger.info("加载配置项出错：agentUrl = " + agentUrl + " 、userId = "
                    + userId + "、smsAuthKey = " + smsAuthKey);
            return flag;
        }
        
        Map<String, Object> paramObj = new HashMap<String, Object>();
        Map<String, Object> msgBody = new HashMap<String, Object>();
       // JsonObject msgBody = new JsonObject();
        msgBody.put("msg", (String) map.get("msg"));
        msgBody.put("caseId", (String) map.get("caseId"));
        paramObj.put("agentUrl", agentUrl);
        paramObj.put("userId", userId);
        paramObj.put("smsAuthKey", smsAuthKey);
        paramObj.put("pushMode", 0);
        paramObj.put("pushId", (String) map.get("pushId"));
        paramObj.put("appName", (String) map.get("appName"));
        paramObj.put("msgBody", msgBody);
        map.put("cmd", cmd);
        map.put("params", paramObj);
        params = JacksonUtil.mapToJson(map);
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(agentUrl);
        try
        {
            @SuppressWarnings("deprecation")
			String encoderJson = URLEncoder.encode(params,
                    HTTP.UTF_8);
            post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            StringEntity se = new StringEntity(encoderJson);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    APPLICATION_JSON));
            post.setEntity(se);
            HttpResponse response = client.execute(post);
            String entity = EntityUtils.toString(response.getEntity());
            JSONObject json = JSONObject.fromObject(entity);
            String result = json.getString("result");
            if (!StringTools.isEmptyOrNull(result)
                    && resultSuccessCode.equals(result))
            {
                flag = true;
            }
            logger.info("调用PushApi平台下推送信息返回值：" + entity);
        }
        catch (UnsupportedEncodingException e1)
        {
            logger.error(e1);
        }
        catch (ClientProtocolException e)
        {
            logger.error(e);
        }
        catch (IOException e)
        {
            logger.error(e);
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
        return flag;
    }
    
}
