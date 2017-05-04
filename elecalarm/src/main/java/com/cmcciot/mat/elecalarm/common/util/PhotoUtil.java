package com.cmcciot.mat.elecalarm.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class PhotoUtil
{
    public static Map<String, String> getUploadUrl(String urlPath,
            String dateString) throws Exception
    {
        String entity = "";
        String url = "";
        String seqID = "";
        //2.调用服务管理
        HttpClient client = new DefaultHttpClient();
        //2.1.根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost(urlPath);
        // 添加http请求参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("devID", "matfile"));
        
        nvps.add(new BasicNameValuePair("contentID", "matfile_" + dateString
                + "_jpg"));
        
        nvps.add(new BasicNameValuePair("urlType", "0"));
        
        post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        
        //3.放置header信息
        StringBuilder headerMsg = new StringBuilder();
        //设置AUTH
        headerMsg.append("isAuth=\"true\"");
        //设置KEY
        headerMsg.append(",key=\"c57776662ba11b9e855e253f0a2fa25d\"");
        //设置serverID
        headerMsg.append(",serverID=\"bbbbb\"");
        
        post.setHeader("HOA_auth", headerMsg.toString());
        //post.addHeader("HOA-auth", headerMsg.toString());
        //2.3.执行http请求
        //        HttpServletResponse response = null;
        HttpResponse httpResponse = null;
        try
        {
            httpResponse = client.execute(post);
            
            //3.获取服务管理返回值
            
            entity = EntityUtils.toString(httpResponse.getEntity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            client.getConnectionManager().shutdown();
        }
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1 = JacksonUtil.jsonToMap(entity);
        seqID = (String) map1.get("contentID");
        url = (String) map1.get("URL");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", url);
        map.put("seqID", seqID);
        return map;
    }
    
    public static String getDownloadUrl(String seqID, String urlPath)
            throws Exception
    {
        String entity = "";
        String url = "";
        //2.调用服务管理
        HttpClient client = new DefaultHttpClient();
        //2.1.根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost(urlPath);
        // 添加http请求参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("devID", "matfile"));
        nvps.add(new BasicNameValuePair("contentID", seqID));
        nvps.add(new BasicNameValuePair("urlType", "1"));
        
        post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        
        //3.放置header信息
        StringBuilder headerMsg = new StringBuilder();
        //设置AUTH
        headerMsg.append("isAuth=\"true\"");
        //设置KEY
        headerMsg.append(",key=\"c57776662ba11b9e855e253f0a2fa25d\"");
        //设置serverID
        headerMsg.append(",serverID=\"bbbbb\"");
        
        post.setHeader("HOA_auth", headerMsg.toString());
        //post.addHeader("HOA-auth", headerMsg.toString());
        //2.3.执行http请求
        //        HttpServletResponse response = null;
        HttpResponse httpResponse = null;
        try
        {
            httpResponse = client.execute(post);
            
            //3.获取服务管理返回值
            
            entity = EntityUtils.toString(httpResponse.getEntity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            client.getConnectionManager().shutdown();
        }
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1 = JacksonUtil.jsonToMap(entity);
        url = (String) map1.get("URL");
        
        return url;
        
    }
    
    public static String photoUpload(String seqID, String uploadUrl,
            String file, String type) throws Exception
    {
        String errorCode = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------293342587424372";
        // 创建连接
        HttpURLConnection connection = null;
        URL url = new URL(uploadUrl);
        connection = (HttpURLConnection) url.openConnection();
        // 设置属性
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "ISO-8859-1");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);
        connection.connect();
        
        // 得到连接的数据写入流
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());
        // 写入起始头
        out.writeBytes(twoHyphens + boundary + lineEnd);
        // 写入描述字段
        out.writeBytes("Content-Disposition: form-data; name=\"" + seqID
                + "\"; filename=\"" + seqID + "\"" + lineEnd);
        out.writeBytes("Content-Type: image/jpeg; charset=ISO-8859-1"
                + "\r\n\r\n");
        // 将file转成字节流
//        byte[] buffer = Base64.decode(file);
        byte[] buffer = Base64.decodeBase64(file);
        int len = buffer.length;
        out.write(buffer, 0, len);
        out.flush();
        // 写入结束符
        out.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
        out.flush();
        out.close();
        // 获得结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        String lines;
        while ((lines = reader.readLine()) != null)
        {
            Map<String, Object> mapReturn = new HashMap<String, Object>();
            mapReturn = JacksonUtil.jsonToMap(lines);
            errorCode = (String) mapReturn.get("errorCode");
        }
        reader.close();
        connection.disconnect();
        return errorCode;
    }
}
