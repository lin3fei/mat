package com.cmcciot.mat.filemanage.timermanager;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmcciot.mat.filemanage.cache.FileCache;
import com.cmcciot.mat.filemanage.cache.Md5Cache;
import com.cmcciot.mat.filemanage.utils.KeyUtil;
import com.cmcciot.mat.filemanage.utils.PropertyUtil;

public class FileManageTimerTask extends TimerTask
{
    private static Log logger = LogFactory.getLog(FileManageTimerTask.class);
    
    @SuppressWarnings("rawtypes")
    @Override
    public void run()
    {
        try
        {
            Iterator uploadIter = FileCache.FILEUPLOADCACHEMAP.entrySet().iterator();
            while (uploadIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) uploadIter.next();
                Md5Cache value = (Md5Cache)entry.getValue();
                String stamptime = value.getTimeTemp();
                String life = PropertyUtil.getValue("life");
                if (KeyUtil.compare_date(life, stamptime) < 0)
                {
                    logger.info("上传时间戳已经过期");
                    uploadIter.remove();
                }
            }
            
            Iterator downloadIter = FileCache.FILEDOWNLOADCACHEMAP.entrySet().iterator();
            while (downloadIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) downloadIter.next();
                Md5Cache value = (Md5Cache)entry.getValue();
                String stamptime = value.getTimeTemp();
                String life = PropertyUtil.getValue("life");
                if (KeyUtil.compare_date(life, stamptime) < 0)
                {
                    logger.info("下载时间戳已经过期");
                    downloadIter.remove();
                }
            }
            
            System.out.println("清空缓存监控！");
        }
        catch (Exception e)
        {
            logger.info("-------------解析信息发生异常--------------");
        }
    }
}
