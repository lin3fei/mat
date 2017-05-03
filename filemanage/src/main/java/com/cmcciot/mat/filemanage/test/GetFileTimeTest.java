package com.cmcciot.mat.filemanage.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/** 
 * 读取文件创建时间和最后修改时间 
 */
public class GetFileTimeTest
{
    
    public static void main(String[] args)
    {
        String path = "E:/test";
        getDate(path);
    }
    
    @SuppressWarnings("static-access")
    public static void getDate(String path)
    {
        File f = new File(path);
        if (!f.exists())
        {
            System.out.println("资源主目录不存在");
        }
        if (f.isDirectory())
        {
            File files[] = f.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (!files[i].getName().equals("apk"))
                {
                    getDate(files[i].getAbsolutePath());
                }
            }
        }
        else if (f.isFile())
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(f.lastModified()));
            calendar.add(calendar.HOUR, 24);
            Date resultDate = calendar.getTime();
            Date curentDate = new Date();
            System.out.println(resultDate + "********" + curentDate);
            System.out.println(resultDate.getTime() < curentDate.getTime() ? "过期"
                    : "aaaaaaa");
            if (resultDate.getTime() < curentDate.getTime())
            {
                boolean delResult = f.delete();
                System.out.println("删除过期的文件：" + delResult);
            }
        }
    }
}