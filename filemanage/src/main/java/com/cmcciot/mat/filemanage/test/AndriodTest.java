package com.cmcciot.mat.filemanage.test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class AndriodTest
{
    
//    private static final String TAG = "uploadFile";
    
    private static final int TIME_OUT = 10 * 10000000; // 超时时间  
    
    private static final String CHARSET = "UTF-8"; // 设置编码  
    
    public static final String SUCCESS = "1";
    
    public static final String FAILURE = "0";
    
    public static void main(String[] args)
    {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成  
        String PREFIX = "--";
        String LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型  
        String RequestURL = "http://10.189.24.118:8081/filemanage/douya?devID=douya00000&stamptime=201405051651390490&life=1800&cry=6ec8326fb1524cb592083caa2579e124";
        File file = new File("E:/hello.jpg");
        try
        {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流  
            conn.setDoOutput(true); // 允许输出流  
            conn.setUseCaches(false); // 不允许使用缓存  
            conn.setRequestMethod("POST"); // 请求方式  
            conn.setRequestProperty("Charset", CHARSET); // 设置编码  
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (file != null)
            {
                /** 
                 * 当文件不为空，把文件包装并且上传 
                 */
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                
                //写入起始头
                dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
                //写入描述字段
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + "uploadImage" + "\"; filename=\"" + "test.jpg" + "\""
                        + LINE_END);
                dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /** 
                 * 获取响应码 200=成功 当响应成功，获取响应的流 
                 */
                int res = conn.getResponseCode();
                if (res == 200)
                {
                    System.out.println("====" + SUCCESS);
                }
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
