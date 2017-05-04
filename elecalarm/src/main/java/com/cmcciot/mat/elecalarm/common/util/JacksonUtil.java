package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * json工具类
 * 
 * @author  songhuabiao
 * @version  [版本号, 2014年5月12日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class JacksonUtil
{
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * json转obj
     * <一句话功能简述>
     * <功能详细描述>
     * @param json
     * @param cls
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object jsonToObj(String json, Class cls)
            throws InstantiationException, IllegalAccessException,
            JsonParseException, JsonMappingException, IOException
    {
//        objectMapper = new ObjectMapper();
        Object obj = objectMapper.readValue(json, cls);
        return obj;
    }
    
    /**
     * obj转json
     * <一句话功能简述>
     * <功能详细描述>
     * @param obj
     * @return
     * @throws IOException [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("deprecation")
    public static String objToJson(Object obj) throws IOException
    {
//        objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        JsonGenerator gener;
        gener = new JsonFactory().createJsonGenerator(sw);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.writeValue(gener, obj);
        gener.close();
        String json = sw.toString();
        return json;
    }
    
    /**
     * map转化为json
     * <功能详细描述>
     * @param map
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public static String mapToJson(Map map)
    {
        String json = "{\"errorCode\":1,\"description\":\"服务器内部异常\"}";
//        objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        JsonGenerator gener;
        try
        {
            gener = new JsonFactory().createJsonGenerator(sw);
            objectMapper.writeValue(gener, map);
            gener.close();
            json = sw.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return json;
    }
    
    /**
     * json转化为map
     * @param json
     * @return [参数说明]
     * 
     * @return Map<String,Object> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jsonToMap(String json)
    {
        Map<String, Object> map = new HashMap<String, Object>();
//        ObjectMapper mapper = new ObjectMapper();
        try
        {
            map = objectMapper.readValue(json, Map.class);
            
            for (String s : map.keySet())
            {
                String type = map.get(s).getClass().getSimpleName();
                //将integer类型的转化为String
                if (type.equals("Integer"))
                {
                    map.put(s, ((Integer) map.get(s)).toString());
                }
                //将long类型的转化为String
                if (type.equals("Long"))
                {
                    map.put(s, ((Long) map.get(s)).toString());
                }
            }
        }
        catch (Exception e)
        {
            logger.error("消息格式错误:"+json+"-----"+e);
            map = new HashMap<String, Object>();
            map.put("jsonError", "消息格式错误");
        }
        return map;
    }
    
    /**
     * 往json字串里添加属性
     * @param jsonStr
     * @param params
     * @param values
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);
    
    public static String putJson(String jsonStr, String[] params,
            String[] values)
    {
        if (jsonStr.equals(""))
            jsonStr = "{}";
        try
        {
            JSONObject obj = JSONObject.fromObject(jsonStr);
            if (params != null)
            {
                if (params.length != values.length)
                {
                    obj.put("errorCode", 01);
                    obj.put("description", "服务器内部错误");
                    return obj.toString();
                }
                for (int i = 0; i < params.length; i++)
                {
                    if (obj.containsKey(params[i]))//如果存在属性，则不覆盖
                        continue;
                    if (params[i].equals("version")
                            || params[i].equals("errorCode"))
                    {
                        
                        int val = 0;
                        try
                        {
                            //兼容16进制字符串
                            if (values[i].indexOf("0x") > -1)
                            {
                                val = Integer.parseInt(values[i].replace("0x",
                                        ""), 16);
                            }
                            else
                            {
                                val = Integer.parseInt(values[i]);
                            }
                            obj.put(params[i], val);
                        }
                        catch (Exception e)
                        {
                            obj.put(params[i], values[i]);
                        }
                    }
                    else
                    {
                        obj.put(params[i], values[i]);
                    }
                }
            }
            return obj.toString();
        }
        catch (Exception e)
        {
            logger.error("参数json为：" + jsonStr + "params.length="
                    + params.length + "||value.length=" + values.length+"----错误:"+e);
            return "{\"errorCode\"：2,\"description\"：\"数据格式错误\"}";
        }
    }
    
    /**
     * 往json字串里删除属性
     * <功能详细描述>
     * @param jsonStr
     * @param params
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String removeJson(String jsonStr, String... params)
    {
        if (jsonStr.equals(""))
            jsonStr = "{}";
        try
        {
            JSONObject obj = JSONObject.fromObject(jsonStr);
            for (int i = 0; i < params.length; i++)
            {
                obj.remove(params[i]);
            }
            return obj.toString();
        }
        catch (Exception e)
        {
            return "{\"errorCode\"：2,\"description\"：\"数据格式错误\"}";
        }
    }
    
    public static String  listToJson(List<?> list){
//        objectMapper = new ObjectMapper();
        String backstring = "";
        try
        {
            backstring = objectMapper.writeValueAsString(list);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return backstring;
    }
    
    /**
     * 获取单个json属性的值
     * <功能详细描述>
     * @param jsonStr
     * @param param
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getJsonValue(String jsonStr, String param)
    {
        if (jsonStr.equals(""))
            jsonStr = "{}";
        String value = "";
        try
        {
            JSONObject obj = JSONObject.fromObject(jsonStr);
            value = obj.get(param).toString();
        }
        catch (Exception e)
        {
            //TODO
        }
        return value;
    }
    
}
