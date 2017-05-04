/*
 * 文 件 名:  HttpInterfaceTools.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  lidechun
 * 修改时间:  2014年5月8日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.sdhttp.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cmcciot.mat.elecalarm.common.exception.ErrorNumber;
import com.cmcciot.mat.elecalarm.common.exception.LogicException;
import com.cmcciot.mat.elecalarm.common.util.JacksonUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.sdhttp.bean.OneNetBackInfo;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  lidechun
 * @version  [版本号, 2014年5月8日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class OneNetHttpInterfaceTools
{
    private static Log logger = LogFactory.getLog(OneNetHttpInterfaceTools.class);
    
    /**
     * 查询数据接口
     * <功能详细描述>
     * @param uri
     * @param apiKey
     * @return
     * @throws LogicException [参数说明]
     * 
     * @return OneNetBackInfo [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static OneNetBackInfo httpGetOneNet(String uri, String apiKey)
            throws LogicException
    {
        HttpClient httpClient = new DefaultHttpClient();
        logger.debug("调用接口地址是：" + uri);
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("api-key", apiKey);
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            OneNetBackInfo backInfo = handleResponseToBean(response);
            
            return backInfo;
            
        }
        catch (Exception e)
        {
            logger.error("调用接口发生异常", e);
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "调用接口发生异常");
        }
        finally
        {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    
    /**
     * 用于调用删除接口
     * <功能详细描述>
     * @param uri
     * @param apiKey
     * @return
     * @throws LogicException [参数说明]
     * 
     * @return OneNetBackInfo [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static OneNetBackInfo httpDeleteOneNet(String uri, String apiKey)
            throws LogicException
    {
        logger.debug("调用接口地址是：" + uri);
        
        // HttpGet httpDelete = new HttpGet(uri);
        HttpClient httpClient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(uri);
        httpDelete.addHeader("api-key", apiKey);
        try
        {
            HttpResponse response = httpClient.execute(httpDelete);
            OneNetBackInfo backInfo = handleResponseToBean(response);
            
            return backInfo;
            
        }
        catch (Exception e)
        {
            logger.error("调用接口发生异常", e);
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "调用接口发生异常");
        }
        finally
        {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    
    /**
     * post请求onenet接口
     * <功能详细描述>
     * @param uri
     * @param apikey
     * @return [参数说明]
     * 
     * @return OneNetBackInfo [返回类型说明]
     * @throws LogicException 
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static OneNetBackInfo httpPostOneNet(String uri, String apiKey,
            final String content) throws LogicException
    {
        logger.debug("调用接口地址是：" + uri);
        logger.debug("请求内容是：" + content);
        if (StringTools.isEmptyOrNull(content))
        {
            logger.debug("请求接口所带内容不能为空");
            throw new LogicException(ErrorNumber.NULL_ERROR, "请求接口所带内容不能为空");
        }
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(uri);
        post.addHeader("api-key", apiKey);
        
        ContentProducer cp = new ContentProducer()
        {
            @Override
            public void writeTo(OutputStream outstream) throws IOException
            {
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(content.toString());
                writer.flush();
            }
        };
        try
        {
            post.setEntity(new EntityTemplate(cp));
            HttpResponse response = httpClient.execute(post);
            OneNetBackInfo backInfo = handleResponseToBean(response);
            
            return backInfo;
            
        }
        catch (Exception e)
        {
            logger.error("调用接口发生异常", e);
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "调用接口发生异常");
        }
        finally
        {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    
    /**
     * <put请求接口>
     * <主要用于更新>
     * @param uri
     * @param apiKey
     * @param content
     * @return
     * @throws LogicException [参数说明]
     * 
     * @return OneNetBackInfo [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static OneNetBackInfo httpPutOneNet(String uri, String apiKey,
            final String content) throws LogicException
    {
        
        logger.debug("调用接口地址是：" + uri);
        logger.debug("请求内容是：" + content);
        if (StringTools.isEmptyOrNull(content))
        {
            logger.debug("请求接口所带内容不能为空");
            throw new LogicException(ErrorNumber.NULL_ERROR, "请求接口所带内容不能为空");
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpPut put = new HttpPut(uri);
        put.addHeader("api-key", apiKey);
        
        ContentProducer cp = new ContentProducer()
        {
            @Override
            public void writeTo(OutputStream outstream) throws IOException
            {
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(content.toString());
                writer.flush();
            }
        };
        try
        {
            put.setEntity(new EntityTemplate(cp));
            HttpResponse response = httpClient.execute(put);
            OneNetBackInfo backInfo = handleResponseToBean(response);
            
            return backInfo;
            
        }
        catch (Exception e)
        {
            logger.error("调用接口发生异常", e);
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "调用接口发生异常");
        }
        finally
        {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
    }
    
    /**
     * 将json数据类型转换成OneNetBackInfo
     * <功能详细描述>
     * @param json
     * @return [参数说明]
     * 
     * @return OneNetBackInfo [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static OneNetBackInfo handleResponseToBean(HttpResponse response)
            throws LogicException
    {
        
        OneNetBackInfo backInfo = null;
        try
        {
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String conResult = EntityUtils.toString(response.getEntity());
                logger.debug("调用设备云接口成功，返回报文是：" + conResult);
                
                conResult = conResult.replace("\"data\":[]", "\"data\":{}")
                        .replace("\"other\":[]", "\"other\":{}");
                backInfo = (OneNetBackInfo) JacksonUtil.jsonToObj(conResult,
                        OneNetBackInfo.class);
            }
            else
            {
                logger.debug("调用onenet http接口异常！返回的状态码是："
                        + response.getStatusLine().getStatusCode() + "=="
                        + EntityUtils.toString(response.getEntity()));
                
            }
            
        }
        catch (Exception e)
        {
            logger.error("报文转换为对象失败", e);
            throw new LogicException(ErrorNumber.ERROR_JSON_TO_OBJECT,
                    "报文转换为对象失败");
        }
        
        return backInfo;
        
    }
    
}
