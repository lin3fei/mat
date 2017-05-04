package com.cmcciot.mat.elecalarm.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;

public class DataPointUtil
{
    
    public static String getDataPoint(String deviceId)
    {
        // 获取platform地址
        String urlPath = PropertyUtil.getValue("httpPlatform") + "platform/getDatapoint";
        // 模拟POST访问
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlPath);
        String entity = "";
        // 添加http请求参数
        try
        {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("deviceId", deviceId));
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            // 执行http请求
            HttpResponse httpResponse = null;
            httpResponse = client.execute(post);
            entity = EntityUtils.toString(httpResponse.getEntity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            post.releaseConnection();
            client.getConnectionManager().shutdown();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map = JacksonUtil.jsonToMap(entity);
        return JacksonUtil.mapToJson(map);
    }
}
