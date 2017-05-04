package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * 向电信、联通号码发短信工具类
 * <功能详细描述>
 * 
 * @author  hy
 * @version  [版本号, 2014年8月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ValidateSmsSender
{
    
    private static Log logger = LogFactory.getLog(ValidateSmsSender.class);
    
    private static Pattern phonePattern = Pattern.compile("^[0-9]{11}$");
    
    /**
     * 短信发送接口方法
     * <功能详细描述>
     * @param phoneNos
     * @param content
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @throws IOException 
     * @throws ClientProtocolException 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean sendMsg(String[] phoneNos, String content)
    {
        boolean flag = false;
        
        //验证手机号
        if (StringTools.isEmptyOrNull(phoneNos) || phoneNos.length > 200
                || !checkPhones(phoneNos) || phoneNos.length < 1)
        {
            
            flag = false;
            
            logger.error("第三方短信网关接口信息：错误信息：手机号码为空或者手机号码个数不在范围内或者手机号码格式不对，有效电话号码个数为[1-200]!");
        }
        //验证短信内容
        else if (StringTools.isEmptyOrNull(content) || content.length() > 500)
        {
            flag = false;
            
            logger.error("第三方短信网关接口信息：错误信息：短信内容为空或者短信内容长度超过500个字符!");
            
        }
        else
        {
            
            String url = ConfigPropertyTool.getValue("sms.url");
            
            // HttpPost post = new HttpPost(url);
            
            // post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            
            String OperID = ConfigPropertyTool.getValue("sms.OperID");
            
            String OperPass = AESTools.decrypt(ConfigPropertyTool.getValue("sms.OperPass"));
            
            String SendTime = "";
            
            String ValidTime = "";
            
            String AppendID = "";
            
            String DesMobile = phoneNoToString(phoneNos);
            
            String Content = content;
            
            try
            {
                Content = URLEncoder.encode(Content, "GBK");
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("发送短信错误：短信内容转码错误！");
            }
            
            //以长短信的形式发
            String ContentType = "8";
            
            String uri = "?";
            
            uri += "OperID=" + OperID + "&";
            
            uri += "OperPass=" + OperPass + "&";
            
            uri += "SendTime=" + SendTime + "&";
            
            uri += "ValidTime=" + ValidTime + "&";
            
            uri += "AppendID=" + AppendID + "&";
            
            uri += "DesMobile=" + DesMobile + "&";
            
            uri += "Content=" + Content + "&";
            
            uri += "ContentType=" + ContentType;
            
            logger.debug("准备发送短信：短信发送url为：" + url + uri);
            
            HttpGet get = new HttpGet(url + uri);
            
            HttpResponse resp;
            try
            {
                resp = new DefaultHttpClient().execute(get);
                
                String respStr = EntityUtils.toString(resp.getEntity());
                
                logger.debug("第三方短信网关连接成功：第三方返回内容：" + respStr);
                
                Document document = XmlTools.string2Document(respStr);
                
                Element root = document.getRootElement();
                
                Element code = root.element("code");
                
                String codeText = code.getText();
                
                if (codeText.equals("01") || codeText.equals("02")
                        || codeText.equals("03"))
                {
                    
                    flag = true;
                    
                    logger.debug("发送短信报文：短信发送成功！");
                    
                }
                else
                {
                    
                    flag = false;
                    
                    logger.debug("发送短信报文：短信发送失败！返回第三方短信网关状态错误码：" + codeText);
                }
            }
            catch (ClientProtocolException e)
            {
                logger.error("发送短信错误：第三方短信网关连接错误！");
            }
            catch (IOException e)
            {
                logger.error("发送短信错误：第三方短信网关响应报文错误！");
            }
            
        }
        
        return flag;
    }
    
    /**
     * 检验电话号码的合法性
     * <功能详细描述>
     * @param phoneNos
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static boolean checkPhones(String[] phoneNos)
    {
        boolean flag = true;
        
        for (String phone : phoneNos)
        {
            
            if (!phonePattern.matcher(phone).matches())
            {
                
                flag = false;
                
                break;
                
            }
            
        }
        
        return flag;
    }
    
    /**
     * 手机号转成规定的字符串
     * <功能详细描述>
     * @param phoneNos
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static String phoneNoToString(String[] phoneNos)
    {
        
        String phones = "";
        
        if (phoneNos.length == 1)
        {
            
            phones = phoneNos[0];
            
        }
        else if (phoneNos.length > 1)
        {
            
            for (String phone : phoneNos)
            {
                
                phones += phone + ",";
                
            }
            
            phones = phones.substring(0, phones.length() - 1);
            
        }
        
        return phones;
        
    }
    
}
