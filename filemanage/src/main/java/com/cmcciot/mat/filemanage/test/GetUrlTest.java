package com.cmcciot.mat.filemanage.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class GetUrlTest
{
    
    public static void main(String[] args) throws ParseException, IOException
    {
        String entity = "";
        //2.调用服务管理
        HttpClient client = new DefaultHttpClient();
        //2.1.根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost("http://127.0.0.1:8083/filemanage/getFirmwareUrl");
        // 添加http请求参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("urlType", "0"));
        nvps.add(new BasicNameValuePair("devID", "matfile"));
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        String dateString = formatter.format(currentTime);
        nvps.add(new BasicNameValuePair("contentID", "matfile_"+dateString+"_zip"));
        try
        {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        
        //3.放置header信息
        StringBuilder headerMsg = new StringBuilder();
        //设置AUTH
        headerMsg.append("isAuth=\"true\"");
        //设置KEY
        headerMsg.append(",key=\"c57776662ba11b9e855e253f0a2fa25d\"");
        
        post.setHeader("HOA_auth", headerMsg.toString());
        //post.addHeader("HOA-auth", headerMsg.toString());
        //2.3.执行http请求
        //        HttpServletResponse response = null;
        HttpResponse httpResponse = null;
        try
        {
            httpResponse = client.execute(post);
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //3.获取服务管理返回值
        entity = EntityUtils.toString(httpResponse.getEntity());
        System.out.println(httpResponse.getStatusLine().getStatusCode());
        System.out.println(entity);
    }
}
