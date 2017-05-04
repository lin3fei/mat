package com.cmcciot.mat.elecalarm.locationmanager.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.locationmanager.bean.OpenApiBean;

@Service("openApiInfo")
public class OpenApiConfig
{
    
    public static List<OpenApiBean> list = new ArrayList<OpenApiBean>();
    
    public void initOpenApiConfig()
    {
//        list = openApiDao.getOpenApiConfig();
    }
    
    /**
     * 回调open api相关信息放在内存中，获取相应记录的属性值；
     * <功能详细描述>
     * @param type
     * @param value
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getKey(String type, String value)
    {
        String tvalue = "";
        OpenApiBean openApiBean = new OpenApiBean();
        for (int i = 0; i < list.size(); i++)
        {
            openApiBean = list.get(i);
            if (value.equals(openApiBean.getServerID()))
            {
                Field[] fields = openApiBean.getClass().getDeclaredFields();
                for (int j = 0; j < fields.length; j++)
                {
                    String fieldname = fields[j].getName();
                    if(fieldname.equals(type)){
                        tvalue = (String)getValueByName(fieldname,openApiBean);
                        return tvalue;
                    }
                }
                return "";
            }
        }
        return "";
    }
    
    /**
     * 反射获取对象的属性值
     * <功能详细描述>
     * @param fieldName 属性名
     * @param obj 目标对象
     * @return [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static Object getValueByName(String fieldName, Object obj) {  
        try {    
            String firstLetter = fieldName.substring(0, 1).toUpperCase();    
            String getter = "get" + firstLetter + fieldName.substring(1);    
            Method method = obj.getClass().getMethod(getter, new Class[] {});    
            Object value = method.invoke(obj, new Object[] {});    
            return value;    
        } catch (Exception e) {    
            return "";    
        }    
    }  
}
