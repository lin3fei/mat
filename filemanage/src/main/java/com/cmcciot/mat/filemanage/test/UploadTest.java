package com.cmcciot.mat.filemanage.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.ParseException;

public class UploadTest
{
    
    @SuppressWarnings("resource")
    public static void main(String[] args) throws ParseException, IOException
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------293342587424372";
        
        //创建连接
        HttpURLConnection connection = null;
        URL url = new URL(
//                "http://192.168.200.214:8080/filemanage/imageupload/230_201406190911170001_zip/230/201406301551530863/3600/209c30fa39543ccc200a0c95c6e229bb");
                "http://192.168.200.192:8083/filemanage/firmwareUpload/matfile/matfile_201411081534270693_zip/201411081534270983/3600/1bded44d3f12b58543fc73cecf9010e7");
                connection = (HttpURLConnection) url.openConnection();
        //设置属性
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);
        connection.connect();
        
        String photoFilename = "matfile_201411081531350521_zip";
        // 读取文件
        FileInputStream fileInputStream = new FileInputStream("D:\\Axure.zip");
        //得到连接的数据写入流
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());
        //写入起始头
        out.writeBytes(twoHyphens + boundary + lineEnd);
        //写入描述字段
        out.writeBytes("Content-Disposition: form-data; name=\""
                + photoFilename + "\"; filename=\"" + photoFilename + "\""
                + lineEnd);
        out.writeBytes("Content-Type: application/octet-stream; charset=UTF-8"
                + "\r\n\r\n");
        //写入头像图片流
        byte[] buffer = new byte[1024];
        int totalSize = 0;
        int len = 0;
        while ((len = fileInputStream.read(buffer)) != -1)
        {
            out.write(buffer, 0, len);
            out.flush();
            totalSize = totalSize + len;
        }
        System.out.println("上传文件大小:" + totalSize);
        //写入结束符
        out.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
        out.flush();
        out.close();
        
        //获得结果
        int result = connection.getResponseCode();
        System.out.println("===状态码：" + result);
        
        //打印返回信息
        InputStream is = connection.getInputStream();
        BufferedReader bfr = null;
        bfr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String str = bfr.readLine();
        while (str != null)
        {
            System.out.println("===返回信息：" + str);
            str = bfr.readLine();
        }
        //关闭连接
        connection.disconnect();
    }
}