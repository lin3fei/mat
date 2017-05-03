package com.cmcciot.mat.filemanage.timermanager;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmcciot.mat.filemanage.utils.PropertyUtil;

public class FileDeleteTimerTask extends TimerTask
{
    private static Log logger = LogFactory.getLog(FileDeleteTimerTask.class);
    
    private PropertyUtil config = new PropertyUtil();
    
    @SuppressWarnings("static-access")
    @Override
    public void run()
    {
        try
        {
            getDate(config.getValue("path"));
        }
        catch (Exception e)
        {
            logger.info("-------------解析信息发生异常--------------");
        }
    }
    
    @SuppressWarnings("static-access")
    private void getDate(String path)
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
            calendar.add(calendar.HOUR,
                    Integer.parseInt(config.getValue("file.life")));
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
