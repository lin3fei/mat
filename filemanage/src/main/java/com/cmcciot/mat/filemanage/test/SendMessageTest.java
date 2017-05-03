package com.cmcciot.mat.filemanage.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

public class SendMessageTest
{
    
    public static void main(String[] args) throws ParseException, IOException
    {
        String entity = "";
        //2.调用服务管理
        HttpClient client = new DefaultHttpClient();
        //2.1.根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost("http://10.189.24.26:8080/osh/uploadImgFinished");
        
        // 添加http请求参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("devID", "ABCDA12B12301005"));
        nvps.add(new BasicNameValuePair("contentID", "douya_00000_JPG"));
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
        //            System.out.println(response.getOutputStream().toString().length());
    }
    
}
