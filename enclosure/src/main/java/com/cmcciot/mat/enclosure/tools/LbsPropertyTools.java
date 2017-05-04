package com.cmcciot.mat.enclosure.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 读取lbs.properties文件
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年11月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class LbsPropertyTools
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
            configFile = new File(LbsPropertyTools.class.getResource("/lbs.properties").getPath());
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
        if(lm != fileLastModify)
        {
            load();
        }
        return props.getProperty(name);
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
