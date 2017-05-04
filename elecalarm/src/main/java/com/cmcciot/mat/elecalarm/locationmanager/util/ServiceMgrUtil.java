package com.cmcciot.mat.elecalarm.locationmanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.elecalarm.common.ServiceUserKeyConstant;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;

public class ServiceMgrUtil
{
    static Logger logger = LoggerFactory.getLogger(ServiceMgrUtil.class);
    
    /**
     * 经http open api至服务管理，服务管理分发接口时，定义的内部规则
     * KEY 接收的消息类型，value 为响应消息类型
     */
    private static Properties props = new Properties();
    
    //需要鉴权的接口
    public static Map<String, String> methodAuthMap = new HashMap<String, String>();
    
    //所有接口
    public static Map<String, String> methodMap = new HashMap<String, String>();
    
    /*
     * 回调响应消息途径有两种，直接在服务管理子线程回调api，还有就是在onenet agent 实现类 MessageImpl里面回调api
     */
    public static Map<String, String> methodBackRouterMap = new HashMap<String, String>();
    
    private static File file;
    static
    {
        file = new File(
                PropertyUtil.class.getResource("/serviceMethod.properties")
                        .getPath());
        try
        {
            InputStream is = new FileInputStream(file);
            props.load(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        @SuppressWarnings("rawtypes")
        Enumeration enu = props.propertyNames();
        while (enu.hasMoreElements())
        {
            String key = enu.nextElement().toString();
            String[] value = props.getProperty(key).split("\\|");
            String targetValue = value[1] + "|" + value[2];
            if (value[0].equals("auth"))
            {
                methodAuthMap.put(key, targetValue);
            }
            if (value[3].equals("no"))
            {
                methodBackRouterMap.put(key, targetValue);
            }
            methodMap.put(key, targetValue);
        }
    }
    
    /**
     * head,key获取目标value
     * <功能详细描述>
     * @param head
     * @param key
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getTargetHeadKey(String head, String key)
    {
        String headValue = "";
        String[] heads = head.replaceAll("\"", "").split(",");
        for (int i = 0; i < heads.length; i++)
        {
            String[] singleHead = heads[i].split("=");
            if (key.equals(singleHead[0]))
            {
                headValue = singleHead[1];
            }
        }
        return headValue;
    }
    
    /**
     * 验证是否返回API
     * <功能详细描述>
     * @param msgtype
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("rawtypes")
    public static boolean checkIsBackApi(String msgtype, String backStr)
    {
        /*
         * 判断规则：
         * 一是，消息类型
         * 二是，是定义的消息的类型，返回值errorCode，如果不为空，表示路由器验证没通过，由控制器子线程直接回调，如果为空，表示验证通过，由
         *     onenet agent回调api
         * 两个条件取逻辑与
         */
        boolean flag = false;
        Map map = JacksonUtil.jsonToMap(backStr);
        //从onenet agent 回调api的消息
        flag = ServiceMgrUtil.methodBackRouterMap.containsKey(msgtype);
        
        //判断返回值,
        boolean isbound = map.get("errorCode") == null
                || "".equals(map.get("errorCode"));
        return flag && isbound;
    }
    
    /**
     * 判断是否走鉴权接口
     * <功能详细描述>
     * @param msgType
     * @param head
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean booleanAuth(String msgType, String head)
    {
        //例外：用户注册，获取验证码，用户名是否已注册
        boolean isAuth = false;
        boolean needAuth = false;
        
        //以下接口需要使用鉴权接口
        if (methodAuthMap.containsKey(msgType))
        {
            needAuth = true;
        }
        String hoa_isAuth = "isAuth=\"true\"";
        
        //无须鉴权的业务
        if (!needAuth)
        {
            isAuth = true;
            return isAuth;
        }
        
        //需要鉴权的业务
        if (needAuth && ((head.indexOf(hoa_isAuth) >= 0)))
        {
            isAuth = true;
            return isAuth;
        }
        return isAuth;
    }
    
    /**
     * 服务管理回调open api
     * <功能详细描述>
     * @param client HttpClient
     * @param backStr 返回消息报文
     * @param head 头部信息
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void backOpenApi(String backStr, String head)
    {
        HttpClient client = new DefaultHttpClient();
        final String resStr = backStr;
        String serviceId = ServiceMgrUtil.getTargetHeadKey(head, "serverID");
        String key = ServiceMgrUtil.getTargetHeadKey(head, "key");
        String msgSeq = ServiceMgrUtil.getTargetHeadKey(head, "msgSeq");
        String url = OpenApiConfig.getKey("url", serviceId);
        
        //根据open api请求地址，进行http请求
        logger.debug("response to appagent, url : " + url);
        HttpPost post = new HttpPost(url);
        
        ContentProducer cp = new ContentProducer()
        {
            @Override
            public void writeTo(OutputStream outstream) throws IOException
            {
                //传递响应结果值
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(resStr);
                writer.flush();
            }
        };
        post.setEntity(new EntityTemplate(cp));
        
        StringBuffer sb = new StringBuffer();
        sb.append("key=" + key + ",msgSeq=" + msgSeq + ",serverID=" + serviceId);
        //返回头部
        post.setHeader("HOA_auth", sb.toString());
        
        try
        {
            //执行http请求
            client.execute(post);
        }
        catch (Exception e)
        {
            logger.error("服务管理：回调APPAgent错误,回调url=" + url, e);
        }
        finally
        {
            post.releaseConnection();
            client.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    /**
     * 刷新密码缓存
     * <功能详细描述>
     * @param client
     * @param url [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void clearCache(String url)
    {
        //密码重置，修改密码接口，之后要刷新api的缓存
        HttpClient client = new DefaultHttpClient();
        String apiUrl = "", split = PropertyUtil.getValue("httpOpenApi.project.name"), clearPath = PropertyUtil.getValue("restful.url.clearCache");
        //获取回调地址
        if (!StringTools.isEmptyOrNull(split))
        {
            apiUrl = url.split(split)[0] + split + "/" + clearPath;
        }
        else
        {
            apiUrl = url.split("/", 4)[0] + "/" + url.split("/", 4)[1] + "/"
                    + url.split("/", 4)[2] + "/" + clearPath;
        }
        HttpPost post = new HttpPost(apiUrl);
        try
        {
            //执行http请求
            client.execute(post);
        }
        catch (Exception e)
        {
            logger.error("错误结果：" + e);
            e.printStackTrace();
        }
        finally
        {
            client.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    
    /**
     * 验证手机号格式
     * <功能详细描述>
     * @param phone
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean checkPhone(String phone)
    {
        //移动所有的号段
        Pattern pattern = Pattern.compile("^13\\d{9}||15[0,1,2,7,8,9]\\d{8}||18[2,3,4,7,8]\\d{8}$||147\\d{8}||178\\d{8}");
        if (StringTools.isEmptyOrNull(phone))
            return false;
        
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches())
        {
            return true;
        }
        return false;
    }
    
    /**
     * 服务管理业务分发前的格式检验
     * <功能详细描述>
     * @param map
     * @param head
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String checkMessageBeforeHandOut(Map<String, Object> map,
            String head)
    {
        String msgType = "COMMON", version = "0", msgSeq = "0";
        String description = "error", errorCode = "";
        //消息格式错误(拼写错误)
        if (map.get("jsonError") != null)
        {
            errorCode = ServiceUserKeyConstant.ERRORCODE_FORMAT_ERROR.toString();
            description = "报文格式错误";
            version = ServiceUserKeyConstant.VERSION_0X0010.toString();
            return JacksonUtil.putJson("",
                    new String[] { "description", "errorCode", "version",
                            "msgSeq", "msgType" },
                    new String[] { description, errorCode, version, msgSeq,
                            msgType });
        }
        
        //消息类型错误(非接口消息类型)
        msgType = (String) map.get("msgType");
        version = (String) map.get("version");
        msgSeq = (String) map.get("msgSeq");
        if (!(ServiceMgrUtil.methodMap.containsKey(msgType)))
        {
            errorCode = ServiceUserKeyConstant.ERRORCODE_FORMAT_ERROR.toString();
            description = "消息类型错误";
            return JacksonUtil.putJson("",
                    new String[] { "description", "errorCode", "version",
                            "msgSeq", "msgType" },
                    new String[] { description, errorCode, version, msgSeq,
                            msgType });
        }
        
        //验证消息接口是否鉴权
        boolean isAuth = ServiceMgrUtil.booleanAuth(msgType, head);
        if (!isAuth)
        {
            //响应消息类型
            msgType = ServiceMgrUtil.methodMap.get(msgType).split("\\|")[1];
            description = "该业务需要鉴权";
            errorCode = ServiceUserKeyConstant.ERRORCODE_USERORPAS_ERROR.toString();
            return JacksonUtil.putJson("",
                    new String[] { "description", "errorCode", "version",
                            "msgSeq", "msgType" },
                    new String[] { description, errorCode, version, msgSeq,
                            msgType });
        }
        return "";
    }
    
    public static boolean routerBackApi(String msgtype)
    {
        Map<String, String> map = ServiceMgrUtil.methodBackRouterMap;
        if (map == null)
        {
            return false;
        }
        for (String v : map.values())
        {
            if (v.indexOf(msgtype) > -1)
            {
                return true;
            }
        }
        return false;
    }
    
    public static void main(String[] args)
    {
        String url = "http://10.189.24.154:8060/httpOpenAPI/nio/response/serviceManager";
        String[] urls = url.split("nio");
        System.out.println(urls[0] + "||" + urls[1]);
    }
}
