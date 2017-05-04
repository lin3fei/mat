package com.cmcciot.mat.elecalarm.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class BaiduPushPropertyUtil
{
private static Properties props = new Properties();
    
    //配置文件
    private static File configFile;
    
    //配置文件的最后修改时间
    private static long fileLastModify = 0L;
    
    static
    {
        try
        {
            configFile = new File(
                    PropertyUtil.class.getResource("/baiduPush.properties")
                            .getPath());
            load();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 根据配置文件中的key,查找值
     * 
     * @param name
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getValue(String name)
    {
        long lm = configFile.lastModified();
        if (lm != fileLastModify)
        {
            load();
        }
        return props.getProperty(name);
    }
    
    /**
     * 根据,返回多组数据
     * <功能详细描述>
     * @param name
     * @return [参数说明]
     * 
     * @return String[] [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String[] getValue4Array(String name)
    {
        long lm = configFile.lastModified();
        if (lm != fileLastModify)
        {
            load();
        }
        String perperties = props.getProperty(name);
        String[] valueArray = null;
        if (!StringTools.isEmptyOrNull(perperties))
        {
            valueArray = perperties.split(",");
        }
        return valueArray;
    }
    
    /**
     * 重新加载配置文件配置项
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static void load()
    {
        try
        {
            InputStream is = new FileInputStream(configFile);
            props.load(is);
            fileLastModify = configFile.lastModified();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
