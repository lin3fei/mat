package com.cmcciot.mat.elecalarm.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * <调用短彩信能力平台下发短信工具类>
 * <功能详细描述>
 * 
 * @author  wl
 * @version  [版本号, 2014年12月2日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class SmsSendUtil
{
    private static Log logger = LogFactory.getLog(ValidateSmsSender.class);
    
    private static Pattern phonePattern = Pattern.compile("^[\\d]{11}|[\\d]{13}$");
    
    private static String siCode = ConfigPropertyTool.getValue("sms.siCode");
    
    private static String senderNo = ConfigPropertyTool.getValue("sms.senderNo");
    
    private static String productCode = ConfigPropertyTool.getValue("sms.productCode");
    
    private static String dcxUrl = ConfigPropertyTool.getValue("sms.dcxUrl");
    
    //下发短信成功返回码
    private static String resultSuccessCode = "10101";
    
    /**
     * 检验电话号码的合法性
     * <功能详细描述>
     * @param phoneNo
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean validatePhones(String phoneNo)
    {
        boolean flag = false;
        
        if (phonePattern.matcher(phoneNo).matches())
        {
            flag = true;
        }
        return flag;
    }
    
    /**
     * <调用短彩信能力平台下发短信方法>
     * <功能详细描述>
     * @param phoneNo
     * @param content
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean sendSmsParam(String phoneNo, String content)
    {
        boolean flag = false;
        //判断配置项
        if (StringTools.isEmptyOrNull(dcxUrl)
                || StringTools.isEmptyOrNull(siCode)
                || StringTools.isEmptyOrNull(senderNo)
                || StringTools.isEmptyOrNull(productCode))
        {
            logger.info("加载配置项出错：dcxUrl = " + dcxUrl + " 、siCode = " + siCode
                    + "、senderNo = " + senderNo + "、productCode = "
                    + productCode);
            return flag;
        }
        
        if (StringTools.isEmptyOrNull(phoneNo) || !validatePhones(phoneNo))
        {
            logger.info("电话号码为空或者格式不对");
            return flag;
        }
        if (StringTools.isEmptyOrNull(content))
        {
            logger.info("短信类容不能为空");
            return flag;
        }
        
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(dcxUrl);
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("siCode", siCode));
        parameters.add(new BasicNameValuePair("smsContent", content));
        parameters.add(new BasicNameValuePair("receiverSingle", phoneNo));
        parameters.add(new BasicNameValuePair("productCode", productCode));
        parameters.add(new BasicNameValuePair("senderNo", senderNo));
        
        UrlEncodedFormEntity requestEntity;
        try
        {
            requestEntity = new UrlEncodedFormEntity(parameters, "utf-8");
            
            post.setEntity(requestEntity);
            
            HttpResponse response = client.execute(post);
            String entity = EntityUtils.toString(response.getEntity());
            
            JSONObject json = JSONObject.fromObject(entity);
            
            String result = json.getString("result");
            if (!StringTools.isEmptyOrNull(result)
                    && resultSuccessCode.equals(result))
            {
                flag = true;
            }
            logger.info("调用短彩信平台接口返回值：" + entity);
        }
        catch (UnsupportedEncodingException e1)
        {
            logger.error(e1);
        }
        catch (ClientProtocolException e)
        {
            logger.error(e);
        }
        catch (IOException e)
        {
            logger.error(e);
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
        return flag;
    }
    
}
