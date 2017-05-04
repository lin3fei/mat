package com.cmcciot.mat.elecalarm.common.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonPluginsUtil
{
    
    /**
     * 从一个JSON 对象字符格式中得到一个java对象
     * 
     * @param jsonString
     * @param beanCalss
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBean(String jsonString, Class<T> beanCalss)
    {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        T bean = (T) JSONObject.toBean(jsonObject, beanCalss);
        return bean;
    }
    
    /**
     * 将java对象转换成json字符串
     * 
     * @param bean
     * @return
     */
    public static String beanToJson(Object bean)
    {
        JSONObject json = JSONObject.fromObject(bean);
        return json.toString();
    }
    
    /**
     * 将java对象List集合转换成json字符串
     * 
     * @param beans
     * @return
     */
    public static String beanListToJson(@SuppressWarnings("rawtypes") List beans)
    {
        StringBuffer rest = new StringBuffer();
        rest.append("[");
        int size = beans.size();
        for (int i = 0; i < size; i++)
        {
            rest.append(beanToJson(beans.get(i)) + ((i < size - 1) ? "," : ""));
        }
        rest.append("]");
        return rest.toString();
    }
    
    /**
     * 从json对象集合表达式中得到一个java对象列表
     * 
     * @param jsonString
     * @param beanClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonToBeanList(String jsonString,
            Class<T> beanClass)
    {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        T bean;
        int size = jsonArray.size();
        List<T> list = new ArrayList<T>(size);
        for (int i = 0; i < size; i++)
        {
            jsonObject = jsonArray.getJSONObject(i);
            bean = (T) JSONObject.toBean(jsonObject, beanClass);
            list.add(bean);
        }
        return list;
    }
    
}