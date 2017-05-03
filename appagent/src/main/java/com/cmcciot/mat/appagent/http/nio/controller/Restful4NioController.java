package com.cmcciot.mat.appagent.http.nio.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.cmcciot.baidu.model.PushModel;
import com.cmcciot.baidu.model.ResultModel;
import com.cmcciot.baidu.push.PushMsg;
import com.cmcciot.mat.appagent.http.nio.HttpKeyConstant;
import com.cmcciot.mat.appagent.http.nio.threadpool.LockMsgSeq;
import com.cmcciot.mat.appagent.http.nio.threadpool.TemporaryObject;
import com.cmcciot.mat.appagent.http.nio.threadpool.ThreadPoolTask;
import com.cmcciot.mat.appagent.http.nio.threadpool.ThreadPoolTaskBoss;
import com.cmcciot.mat.appagent.http.nio.threadpool.ThreadPoolTaskThird;
import com.cmcciot.mat.common.utils.DateUtil;
import com.cmcciot.mat.common.utils.HttpClientUtils;
import com.cmcciot.mat.common.utils.IpTools;
import com.cmcciot.mat.common.utils.KeyUtil;
import com.cmcciot.mat.common.utils.OautherPropertyUtil;
import com.cmcciot.mat.common.utils.PostParameter;
import com.cmcciot.mat.common.utils.PropertyUtil;
import com.cmcciot.mat.common.utils.StringUtil;
import com.cmcciot.mat.common.utils.alipayrefund.alipay.util.AlipaySubmit;
import com.cmcciot.mat.common.utils.alipayutil.AlipayNotify;

/**
 * 迭代二
 * <功能详细描述>
 *
 * @author Administrator
 * @version [版本号, 2014年5月29日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Controller("restful4Nio")
@RequestMapping
public class Restful4NioController
{
    /**
     * 用于存放序列号,未与服务ID组装(默认从0开始)
     */
    private static LockMsgSeq lockMsgSeq = new LockMsgSeq();
    
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private CacheManager ehCacheManager;
    
    public static void main(String[] arg)
    {
        /*String notify_url = PropertyUtil.getValue("alipay_refund_notify_url");
        //需http://格式的完整路径，不允许加?id=123这类自定义参数

        //卖家支付宝帐户
        String seller_email = "vjqy@139.com";
        //必填

        //退款当天日期
        String refund_date = "2015-04-22 09:05:54";
        //必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01 13:13:13

        //批次号 ((String)map.get("WIDdetail_data"))
        String batch_no = "2015042200001";
        //必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001

        //退款笔数
        String batch_num = "1";
        //必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）

        //退款详细数据
        String detail_data = "2015041700001000410060879223^1^autoreFund";
        //必填，具体格式请参见接口技术文档

        //////////////////////////////////////////////////////////////////////////////////

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "refund_fastpay_by_platform_nopwd");
        sParaTemp.put("partner",
                com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.partner);
        sParaTemp.put("_input_charset",
                com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("seller_email", seller_email);
        sParaTemp.put("refund_date", refund_date);
        sParaTemp.put("batch_no", batch_no);
        sParaTemp.put("batch_num", batch_num);
        sParaTemp.put("detail_data", detail_data);
        sParaTemp.put("seller_user_id",
                com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.partner);
        //建立请求
        String sHtmlText = "";
        try
        {
            sHtmlText = AlipaySubmit.buildRequest("", "", sParaTemp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(sHtmlText);*/
        
    }
    
    /**
     * 鉴权接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/serviceManager")
    public void authServiceManagerNio(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值
            StringBuffer buffer = new StringBuffer();
            InputStream in = request.getInputStream();
            //            BufferedInputStream bis = new BufferedInputStream(in);
            //            byte[] bt = new byte[1024];
            //            int iRead;
            //            while ((iRead = bis.read(bt)) != -1)
            //            {
            //                buffer.append(new String(bt, 0, iRead, "ISO-8859-1"));
            //            }
            //            //接口参数
            //            String postStr = new String(buffer.toString().getBytes("ISO-8859-1"),"UTF-8");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            //logger.info("请求消息:" + postStr);//map.put("jsonError", "消息格式错误");
            Map<String, Object> checkMap = StringUtil.jsonToMap(postStr);
            if ("消息格式错误".equals(checkMap.get("jsonError")))
            {
                String paramErrorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":1,\"description\":\"消息格式错误\"}";
                
                response.getWriter().write(paramErrorResponse);
                
                String dateNow = DateUtil.now();
                logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                        + "返回结果的时间：" + dateNow + "返回结果内容：" + paramErrorResponse);
                //立即返回客户端，并关闭连接
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
            
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + StringUtil.replaceSensInfo(postStr));
            
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder();
            
            //设置AUTH
            headerMsg.append("isAuth=\"true\"");
            //headerMsg.append(",userID=\"" + userID + "\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(postStr,
                    headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            //6.1.删除内存中的去掉的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            if (StringUtil.isEmpty(responseVal))
            {
                //超时返回报文
                responseVal = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            responseVal = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                try
                {
                    //  responseVal = JacksonUtil.jsonToXml(responseVal);
                    response.getWriter().write(responseVal);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                
                String dateNow = DateUtil.now();
                logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                        + "返回结果的时间：" + dateNow + "返回结果内容："
                        + StringUtil.replaceSensInfo(responseVal));
                
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 非鉴权接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/noauth/serviceManager")
    public void noAuthServiceManagerNio(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值
            StringBuffer buffer = new StringBuffer();
            InputStream in = request.getInputStream();
            /* BufferedInputStream bis = new BufferedInputStream(in);
             byte[] bt = new byte[1024];
             int iRead;
             while ((iRead = bis.read(bt)) != -1)
             {
                 buffer.append(new String(bt, 0, iRead, "UTF-8"));
             }
             //接口参数
             String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + StringUtil.replaceSensInfo(postStr));
            
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(postStr,
                    headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                    
                    response.getWriter().write(timeoutResponse);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + StringUtil.replaceSensInfo(responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 服务管理主动调用的北向接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/response/serviceManager")
    public void responseResult(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.获取参数值
        StringBuffer buffer = new StringBuffer();
        InputStream in;
        try
        {
            in = request.getInputStream();
            /* BufferedInputStream bis = new BufferedInputStream(in);
             byte[] bt = new byte[1024];
             int iRead;
             while ((iRead = bis.read(bt)) != -1)
             {
                 buffer.append(new String(bt, 0, iRead,"ISO-8859-1"));
             }
             //接口参数
             String postStr =new String( buffer.toString().getBytes("ISO-8859-1"),"utf-8");*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            //根据请求，取出header中的msgSeq的值
            String[] headerEntries = StringUtil.splitIgnoringQuotes(request.getHeader("HOA_auth"),
                    ',');
            Map<String, String> headerMap = StringUtil.splitEachArrayElementAndCreateMap(headerEntries,
                    "=",
                    "\"");
            //头部序列号
            String msgSeq = headerMap.get("msgSeq");
            response.getWriter()
                    .write("{\"errorCode\":\"0\",\"description\":\"已收到消息\"}");
            //根据msgSeq的值，获得内存中的对象，唤醒锁定的对象,并往对象中赋予结果值
            TemporaryObject temporaryObject = (TemporaryObject) HttpKeyConstant.MEMORYVALUE.get(msgSeq);
            if (temporaryObject != null)
            {//判断为空，则可能是因为已经超时，消息寄存器给删掉了。
                temporaryObject.setResponseVal(postStr);
                
                //因为存放对象的map是hashMap，非线程安全，所以，不用担心线程死锁问题。又而msgSeq的key是唯一的，不涉及到安全问题
                synchronized (temporaryObject)
                {//唤醒对象
                    temporaryObject.notifyAll();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 清空鉴权的 用户信息缓存
     * 服务管理主动调用北向接口，修改密码成功后调用此接口清空缓存
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/nio/userCache/clear")
    public void clearCacheResult(HttpServletRequest request,
            HttpServletResponse response)
    {
        try
        {
            this.ehCacheManager.getCache("userInfoCache").flush();
            logger.info("刷新 openapi用户信息缓存成功.");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * <platform主动调用进行第三方平台鉴权的接口>
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/nio/thirdPlatformAuth")
    public void thirdPlatformAuth(HttpServletRequest request,
            HttpServletResponse response)
    {
        String ip = IpTools.getIpAddr(request);
        logger.info("第三方平台：远程请求地址是：" + ip);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        // String Msg = null;
        try
        {
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            /*BufferedInputStream bis = new BufferedInputStream(in);
            byte[] bt = new byte[1024];
            int iRead;
            while ((iRead = bis.read(bt)) != -1)
            {
                buffer.append(new String(bt, 0, iRead, "UTF-8"));
            }

            //接口参数
            String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            Map<String, Object> map = new HashMap<String, Object>();
            map = StringUtil.jsonToMap(postStr);
            String loginType = (String) map.get("loginType");
            String code = (String) map.get("code");
            String appid = (String) map.get("appid");
            String appsecret = (String) map.get("appsecret");
            String thirdPartyToken = (String) map.get("thirdPartyToken");
            String thirdPartyID = (String) map.get("thirdPartyID");
            if (StringUtil.isEmpty(loginType) || StringUtil.isEmpty(appid)
                    || StringUtil.isEmpty(appsecret))
            {
                logger.info("参数loginType和code和appsecret和appid不能为空");
                throw new RuntimeException();
            }
            if (StringUtil.isEmpty(code) && StringUtil.isEmpty(thirdPartyToken))
            {
                logger.info("第三方登录,authCode和thirdPartyToken不能同时为空");
                throw new RuntimeException();
            }
            if (!StringUtil.isEmpty(loginType) && "Weibo".equals(loginType))
            {
                String access_token = "";
                String uid = "";
                //获取AccessToken
                if (!StringUtil.isEmpty(code))
                {//当传了authcode时，用code去取AccessToken
                    String msgStr = HttpClientUtils.weiboHttpPost(code,
                            new PostParameter[] {
                                    new PostParameter("client_id", appid),
                                    new PostParameter("client_secret",
                                            appsecret),
                                    new PostParameter("grant_type",
                                            "authorization_code"),
                                    new PostParameter("code", code),
                                    new PostParameter(
                                            "redirect_uri",
                                            OautherPropertyUtil.getValue("weibo_redirect_URI")) });
                    Map<String, Object> accessTokenReturnMap = new HashMap<String, Object>();
                    accessTokenReturnMap = StringUtil.jsonToMap(msgStr);
                    access_token = (String) accessTokenReturnMap.get("access_token");
                    uid = (String) accessTokenReturnMap.get("uid");
                    if (StringUtil.isEmpty(access_token)
                            || StringUtil.isEmpty(uid))
                    {
                        logger.info("获取access_token失败:" + msgStr);
                        throw new RuntimeException();
                    }
                }
                else if (!StringUtil.isEmpty(thirdPartyToken)
                        && !StringUtil.isEmpty(thirdPartyID))
                {
                    access_token = thirdPartyToken;
                    uid = thirdPartyID;
                }
                else
                {
                    logger.info("微博登陆，参数错误");
                    throw new RuntimeException();
                }
                
                //用得到的access_token和uid获取用户信息
                String userinfoStr = HttpClientUtils.weiboHttpGet(new PostParameter[] { new PostParameter(
                        "uid", uid) },
                        access_token);
                if (StringUtil.isEmpty(userinfoStr))
                {
                    logger.info("获取用户信息失败:" + userinfoStr);
                    throw new RuntimeException();
                }
                returnMap.put("errorCode", "0");
                returnMap.put("Msg", userinfoStr);
                returnMap.put("uid", uid);
            }
            else if (!StringUtil.isEmpty(loginType) && "QQ".equals(loginType))
            {
                String qq_grant_type = OautherPropertyUtil.getValue("qq_grant_type") == null ? "authorization_code"
                        : OautherPropertyUtil.getValue("qq_grant_type");
                String qq_redirect_uri = OautherPropertyUtil.getValue("qq_redirect_uri") == null ? "http://127.0.0.1:8080/appagent/nio/thirdPlatformRedirct"
                        : OautherPropertyUtil.getValue("qq_redirect_uri");
                String qq_accessToken_url = OautherPropertyUtil.getValue("qq_accessToken_url") == null ? "https://graph.qq.com/oauth2.0/token"
                        : OautherPropertyUtil.getValue("qq_accessToken_url");
                String qq_openId_url = OautherPropertyUtil.getValue("qq_openId_url") == null ? "https://graph.qq.com/oauth2.0/me"
                        : OautherPropertyUtil.getValue("qq_openId_url");
                String qq_getuserinfo_url = OautherPropertyUtil.getValue("qq_getuserinfo_url") == null ? "https://graph.qq.com/user/get_user_info"
                        : OautherPropertyUtil.getValue("qq_getuserinfo_url");
                String qq_client_id = appid;
                String qq_client_secret = appsecret;
                String access_token = "";
                //根据code获取accessToken
                if (!StringUtil.isEmpty(code))
                {//当传了authcode时，用code去取AccessToken
                    String accessTokenMsg = HttpClientUtils.qqHttpGet(qq_accessToken_url,
                            new PostParameter[] {
                                    new PostParameter("grant_type",
                                            qq_grant_type),
                                    new PostParameter("client_id", qq_client_id),
                                    new PostParameter("client_secret",
                                            qq_client_secret),
                                    new PostParameter("code", code),
                                    new PostParameter("redirect_uri",
                                            qq_redirect_uri) });
                    Map<String, Object> accessTokenReturnMap = new HashMap<String, Object>();
                    accessTokenReturnMap = StringUtil.jsonToMap(accessTokenMsg);
                    access_token = (String) accessTokenReturnMap.get("access_token");
                    //根据access_token获取openid
                    if (StringUtil.isEmpty(access_token))
                    {
                        logger.info("获取access_token失败:" + accessTokenMsg);
                        throw new RuntimeException();
                    }
                }
                else if (!StringUtil.isEmpty(thirdPartyToken))
                {
                    access_token = thirdPartyToken;
                }
                else
                {
                    logger.info("微博登陆，参数错误");
                    throw new RuntimeException();
                }
                
                String openidMsg = HttpClientUtils.qqHttpGet(qq_openId_url,
                        new PostParameter[] { new PostParameter("access_token",
                                access_token) });
                Map<String, Object> openidReturnMap = new HashMap<String, Object>();
                openidReturnMap = StringUtil.jsonToMap(openidMsg.substring(openidMsg.indexOf("{"),
                        openidMsg.indexOf("}") + 1));
                String openid = (String) openidReturnMap.get("openid");
                String client_id = (String) openidReturnMap.get("client_id");
                //根据openid获取userinfo
                if (StringUtil.isEmpty(openid) || StringUtil.isEmpty(client_id))
                {
                    logger.info("获取openid失败:" + openidMsg);
                    throw new RuntimeException();
                }
                String userinfoMsg = HttpClientUtils.qqHttpGet(qq_getuserinfo_url,
                        new PostParameter[] {
                                new PostParameter("access_token", access_token),
                                new PostParameter("oauth_consumer_key",
                                        client_id),
                                new PostParameter("openid", openid),
                                new PostParameter("format", "json") });
                if (StringUtil.isEmpty(userinfoMsg))
                {
                    logger.info("获取用户信息失败:" + userinfoMsg);
                    throw new RuntimeException();
                }
                returnMap.put("errorCode", "0");
                returnMap.put("Msg", userinfoMsg);
                returnMap.put("uid", openid);
            }
            else if (!StringUtil.isEmpty(loginType)
                    && "Webchat".equals(loginType))
            {
                String weixin_access_token_uri = OautherPropertyUtil.getValue("weixin_access_token_uri") == null ? "https://api.weixin.qq.com/sns/oauth2/access_token"
                        : OautherPropertyUtil.getValue("weixin_access_token_uri");
                String weixin_userinfo_uri = OautherPropertyUtil.getValue("weixin_userinfo_uri") == null ? "https://api.weixin.qq.com/sns/userinfo"
                        : OautherPropertyUtil.getValue("weixin_userinfo_uri");
                String grant_type = OautherPropertyUtil.getValue("grant_type") == null ? "authorization_code"
                        : OautherPropertyUtil.getValue("grant_type");
                //根据code获取accessToken
                String access_token = "";
                String openid = "";
                //获取AccessToken
                if (!StringUtil.isEmpty(code))
                {//当传了authcode时，用code去取AccessToken
                    String accessTokenMsg = HttpClientUtils.qqHttpGet(weixin_access_token_uri,
                            new PostParameter[] {
                                    new PostParameter("grant_type", grant_type),
                                    new PostParameter("appid", appid),
                                    new PostParameter("secret", appsecret),
                                    new PostParameter("code", code), });
                    Map<String, Object> accessTokenReturnMap = new HashMap<String, Object>();
                    accessTokenReturnMap = StringUtil.jsonToMap(accessTokenMsg);
                    access_token = (String) accessTokenReturnMap.get("access_token");
                    openid = (String) accessTokenReturnMap.get("openid");
                    if (StringUtil.isEmpty(access_token)
                            || StringUtil.isEmpty(openid))
                    {
                        logger.info("获取access_token失败:" + accessTokenMsg);
                        throw new RuntimeException();
                    }
                }
                else if (!StringUtil.isEmpty(thirdPartyToken)
                        && !StringUtil.isEmpty(thirdPartyID))
                {
                    access_token = thirdPartyToken;
                    openid = thirdPartyID;
                }
                else
                {
                    logger.info("微博登陆，参数错误");
                    throw new RuntimeException();
                }
                //获取用户信息
                String userinfoMsg = HttpClientUtils.qqHttpGet(weixin_userinfo_uri,
                        new PostParameter[] {
                                new PostParameter("access_token", access_token),
                                new PostParameter("openid", openid) });
                
                //修改微信获取信息乱码问题
                userinfoMsg = new String(userinfoMsg.getBytes("ISO-8859-1"),
                        "UTF-8");
                
                if (StringUtil.isEmpty(userinfoMsg))
                {
                    logger.info("获取用户信息失败:" + userinfoMsg);
                    throw new RuntimeException();
                }
                returnMap.put("errorCode", "0");
                returnMap.put("Msg", userinfoMsg);
                returnMap.put("uid", openid);
            }
            
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
            returnMap.put("errorCode", "1");
        }
        finally
        {
            try
            {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(StringUtil.mapToJson(returnMap));
            }
            catch (IOException e)
            {
            	logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * <第三方平台回调接口>
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/nio/thirdPlatformRedirct")
    public void thirdPlatformRedirct(HttpServletRequest request,
            HttpServletResponse response)
    {
        try
        {
            response.getWriter()
                    .write("{\"errorCode\":\"0\",\"description\":\"已收到消息\"}");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 健康模块-地址转发
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/nio/healthRouteOuter")
    public void healthRouteOuter(HttpServletRequest request,
            HttpServletResponse response)
    {
        String responseContent = null;
        try
        {
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            /* BufferedInputStream bis = new BufferedInputStream(in);
             byte[] bt = new byte[1024];
             int iRead;
             while ((iRead = bis.read(bt)) != -1)
             {
                 buffer.append(new String(bt, 0, iRead, "UTF-8"));
             }

             //接口参数
             String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            Map<String, Object> map = new HashMap<String, Object>();
            map = StringUtil.jsonToMap(postStr);
            
            String requestUrl = (String) map.get("_request_url");
            
            map.remove("_request_url");
            
            String requestContent = StringUtil.mapToJson(map);
            
            //获取用户信息
            responseContent = HttpClientUtils.healthHttpPost(requestUrl,
                    requestContent);
            
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
        finally
        {
            
            try
            {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(responseContent);
            }
            catch (IOException e)
            {
            	logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 健康模块-地址转发Get协议
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/nio/healthRouteOuterGet")
    public void healthRouteOuterGet(HttpServletRequest request,
            HttpServletResponse response)
    {
        
        String responseContent = null;
        try
        {
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close(); //关闭数据流
            Map<String, Object> map = StringUtil.jsonToMap(postStr);
            String requestUrl = (String) map.get("_request_url");
            
            //获取信息
            responseContent = HttpClientUtils.healthHttpGet(requestUrl);
            
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
        finally
        {
            try
            {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(responseContent);
            }
            catch (IOException e)
            {
            	logger.error(e.getMessage(), e);
            }
        }
    }
    /**
     * 健康模块-地址转发
     * <功能详细描述>
     * from表单格式转发
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/nio/healthRouteOuterWithFormData")
    public void healthRouteOuterWithFormData(HttpServletRequest request,
    		HttpServletResponse response) {
    	String responseContent = null;
    	try {
    		StringBuffer buffer = new StringBuffer();
    		InputStream in;
    		
    		in = request.getInputStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(
    				in, "UTF-8"));
    		String line = null;
    		while ((line = reader.readLine()) != null) {
    			buffer.append(line);
    		}
    		String postStr = buffer.toString();
    		reader.close();//关闭数据流
    		Map<String, Object> map = new HashMap<String, Object>();
    		map = StringUtil.jsonToMap(postStr);
    		
    		String requestUrl = (String) map.get("_request_url");
    		
    		map.remove("_request_url");
    		
    		StringBuffer sbf = new StringBuffer();
    		for(String key :  map.keySet()){
    			sbf.append("&");
    			sbf.append(key);
    			sbf.append("=");
    			sbf.append(map.get(key));
    		}
    		String params = "";
    		if(sbf.length() >0){
    			params = sbf.toString().substring(1);
    		}
    		//获取用户信息
    		responseContent = HttpClientUtils.healthHttpPost(requestUrl,params);
    		
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    	} finally {
    		
    		try {
    			response.setContentType("text/html;charset=UTF-8");
    			response.getWriter().write(responseContent);
    		} catch (IOException e) {
    			logger.error(e.getMessage(), e);
    		}
    	}
    }

    @RequestMapping(method = RequestMethod.POST, value = "/nio/baiduPush")
    public void baiduPush(HttpServletRequest request,
            HttpServletResponse response)
    {
    	String uuidlog = UUID.randomUUID().toString();//日志UUID
        String ip = IpTools.getIpAddr(request);
        logger.info(uuidlog+"旧百度推送：远程请求地址是：" + ip);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try
        {
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            Map<String, Object> map = new HashMap<String, Object>();
            logger.info(uuidlog+"旧百度推送,请求报文是：" + postStr);
            map = StringUtil.jsonToMap(postStr);
            String clientUserId = (String) map.get("clientUserId");
            String clientChannelId = (String) map.get("clientChannelId");
            String osType = (String) map.get("osType");
            String apiKey = (String) map.get("apiKey");
            String secretKey = (String) map.get("secretKey");
            String content = (String) map.get("content");
            // String title = (String) map.get("title");
            String baidupushIosState = PropertyUtil.getValue("baidupush_ios_state");
            if (StringUtil.isEmpty(baidupushIosState))
            {
                baidupushIosState = "1";
            }
            if (StringUtil.isEmpty(osType) || StringUtil.isEmpty(apiKey)
                    || StringUtil.isEmpty(secretKey)
                    || StringUtil.isEmpty(content))
            {
                logger.info(uuidlog+"旧百度参数osType和apiKey和secretKey和conten不能为空");
                throw new RuntimeException();
            }
            
            /*
             * @brief 推送单播通知(IOS/Android APNS) message_type = 1 (默认为0)
             */
            ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);
            
            // 2. 创建BaiduChannelClient对象实例
            BaiduChannelClient channelClient = new BaiduChannelClient(pair);
            
            // 3. 若要了解交互细节，请注册YunLogHandler类
            final String ui = uuidlog;
            channelClient.setChannelLogHandler(new YunLogHandler()
            {
                @Override
                public void onHandle(YunLogEvent event)
                {
                	 logger.info(ui+"旧百度onHandle event.getMessage():" + event.getMessage());
                    System.out.println(event.getMessage());
                }
            });
            
            try
            {
                
                // 4. 创建请求类对象
                // 手机端的ChannelId， 手机端的UserId，
                PushUnicastMessageRequest pushRequest = new PushUnicastMessageRequest();
                
                // DeployStatus => 1: Developer 2:
                // Production
                pushRequest.setChannelId(Long.valueOf(clientChannelId));
                pushRequest.setUserId(clientUserId);
                
                if ("1".equals(osType))
                {
                    pushRequest.setDeviceType(Integer.valueOf(3)); // device_type => 1: web 2: pc 3:android // 4:ios 5:wp
                    pushRequest.setMessage(content);
                    pushRequest.setMessageType(0);
                    pushRequest.setDeployStatus(2);
                }
                else if ("2".equals(osType))
                {
                    pushRequest.setDeviceType(Integer.valueOf(4)); // device_type => 1: web 2: pc 3:android // 4:ios 5:wp
                    pushRequest.setMessage(content);
                    pushRequest.setMessageType(1);
                    if ("1".equals(baidupushIosState))
                    {
                        pushRequest.setDeployStatus(1);
                    }
                    else
                    {
                        pushRequest.setDeployStatus(2);
                    }
                }
                // 5. 调用pushMessage接口
                PushUnicastMessageResponse pushResponse = channelClient.pushUnicastMessage(pushRequest);
                
                // 6. 认证推送成功
                //                System.out.println("push amount : "
                //                        + pushResponse.getSuccessAmount());
                logger.info(uuidlog+"旧百度提送返回信息：" + pushResponse.getSuccessAmount());
                returnMap.put("errorCode", "0");
            }
            catch (ChannelClientException e)
            {
            	logger.error(e.getMessage(), e);
                throw new RuntimeException();
            }
            catch (ChannelServerException e)
            {
            	if(null != e){
                logger.info(uuidlog+"旧百度"+(String.format("request_id: %d, error_code: %d, error_message: %s",
                        e.getRequestId(),
                        e.getErrorCode(),
                        StringUtils.isNotBlank(e.getErrorMsg())?e.getErrorMsg():"未知错误信息")));
                }
                throw new RuntimeException();
            }
        }
        catch (Exception e)
        {
            logger.error(uuidlog+"旧百度"+e.getMessage(),e);
            returnMap.put("errorCode", "1");
        }
        finally
        {
            try
            {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(StringUtil.mapToJson(returnMap));
            }
            catch (IOException e)
            {
                logger.error(uuidlog+"旧百度"+e.getMessage(),e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/nio/baiduPushNew")
    public void baiduPushNew(HttpServletRequest request,
            HttpServletResponse response)
    {
    	String uuidlog = UUID.randomUUID().toString();//日志UUID
        String ip = IpTools.getIpAddr(request);
        logger.info(uuidlog+"百度推送：远程请求地址是：" + ip);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try
        {
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            logger.info(uuidlog+"百度推送,请求报文是：" + postStr);
            Map<String, Object> map = new HashMap<String, Object>();
            map = StringUtil.jsonToMap(postStr);
            
            String clientChannelId = (String) map.get("clientChannelId");
            String osType = (String) map.get("osType");//1、Android 2、IOS
            String apiKey = (String) map.get("apiKey");
            String secretKey = (String) map.get("secretKey");
            String msgType = (String) map.get("msgType"); //0：透传消息 1：通知  现在的实现是ios统一为通知，Android自己选择，如果不传本字段，则默认为透传
            String pushType = (String) map.get("pushType");//推送类型，1 单个推送 ，2广播
            String baidupushIosState = PropertyUtil.getValue("baidupush_ios_state");//仅IOS应用推送时使用，1：开发状态2：生产状态
            String baidupushUrl = PropertyUtil.getValue("baidu_push_url");//百度推送api地址
            String msgExpires = (String) map.get("msgExpires");//取值：(0, 86400 x 7]，默认值为3600 x 5 相对于当前时间的消息过期时间，单位为秒 
            String content = null;
            if(StringUtil.isEmpty((String)map.get("userName"))){
            	content = StringUtil.mapToJson((Map<String, Object>)  map.get("content"));
            }else{//为了兼容老的推送，老的推送content传的是字符串
            	content = (String)  map.get("content");
            }
            if (StringUtil.isEmpty(baidupushIosState))
            {
                baidupushIosState = "1";
            }
            if(StringUtil.isEmpty(msgType)){
            	msgType = "0";
            }
            if(StringUtil.isEmpty(pushType)){
            	pushType = "1";
            }
            if(StringUtil.isEmpty(msgExpires)){
            	msgExpires = "18000";
            }
            if (StringUtil.isEmpty(osType) || StringUtil.isEmpty(apiKey)
                    || StringUtil.isEmpty(secretKey)
                    || StringUtil.isEmpty(content))
            {
                logger.info(uuidlog+"参数osType和apiKey和secretKey和conten不能为空");
                throw new RuntimeException();
            }
            	PushModel pm = new PushModel();
            	pm.setApiKey(apiKey);
            	pm.setSecretKey(secretKey);
            	pm.setMsgExpires(Integer.valueOf(msgExpires));
            	pm.setMessage(content);
            	pm.setChannelId(clientChannelId);
            	if(!StringUtil.isEmpty(baidupushUrl)){
            		pm.setChannelRestUrl(baidupushUrl);
            	}
                if ("1".equals(osType))
                {
                	pm.setDeviceType(Integer.valueOf(3)); // device_type => 1: web 2: pc 3:android // 4:ios 5:wp
                	pm.setMsgType(Integer.valueOf(msgType));
                }
                else if ("2".equals(osType))
                {
                	pm.setDeviceType(Integer.valueOf(4)); // device_type => 1: web 2: pc 3:android // 4:ios 5:wp
                	pm.setMsgType(1);//IOS统一为通知消息
                    if ("1".equals(baidupushIosState))
                    {
                    	pm.setDeployStatus(1);
                    }
                    else
                    {
                    	pm.setDeployStatus(2);
                    }
                }
                // 5. 调用推送接口
                if("1".equals(pushType)){//单个
                	pm.setPushType(0);
                }else{//广播
                	pm.setPushType(1);
                }
                PushMsg push = new PushMsg(pm);
                ResultModel rm = push.pushMsg();
                // 6. 认证推送成功
                //                System.out.println("push amount : "
                //                        + pushResponse.getSuccessAmount());
                logger.info(uuidlog+"百度推送返回信息：" + rm.toString());
                returnMap.put("errorCode", rm.isSuccess() ? "0" : "1");//状态码
                returnMap.put("msg", "推送成功");
        }
        catch (Exception e)
        {
            logger.error(uuidlog+e.getMessage(),e);
            returnMap.put("errorCode", "1");
        }
        finally
        {
            try
            {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(StringUtil.mapToJson(returnMap));
            }
            catch (IOException e)
            {
                logger.error(uuidlog+e.getMessage(),e);
            }
        }
    }
    
    /**
     * 第三方平台鉴权接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/serviceThirdManager")
    public void authThirdServiceManagerNio(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值
            StringBuffer buffer = new StringBuffer();
            InputStream in = request.getInputStream();
            /* BufferedInputStream bis = new BufferedInputStream(in);
             byte[] bt = new byte[1024];
             int iRead;
             while ((iRead = bis.read(bt)) != -1)
             {
                 buffer.append(new String(bt, 0, iRead, "UTF-8"));
             }
             //接口参数
             String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            Map<String, Object> checkMap = StringUtil.jsonToMap(postStr);
            
            //消息类型错误(非接口消息类型)
            
            if ("消息格式错误".equals(checkMap.get("jsonError")))
            {
                
                String dateNow = DateUtil.now();
                
                String paramErrorResponse = "{\"respondTime\":dateNow,\"msgType\": \"COMMON\" ,\"msgSeq\" :1,\"errorCode\":2,\"description\":\"消息格式错误\"}";
                
                response.getWriter().write(paramErrorResponse);
                
                logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                        + "返回结果的时间：" + dateNow + "返回结果内容：" + paramErrorResponse);
                //立即返回客户端，并关闭连接
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
            // 春雨医生问题答复且用户标识长度为32时，直接调用在线问诊平台接口（在线问诊平台用户标识统一设置为长度32位的UUID）
            if (StringUtils.equals((String) checkMap.get("msgType"),
                    "MSG_SET_ANSWER_REQ")
                    && StringUtils.length((String) checkMap.get("userName")) == 32)
            {
                logger.info("请求地址：" + IpTools.getIpAddr(request) + "请求内容："
                        + postStr);
                String responseMessage = HttpClientUtils.httpPostWithJson(PropertyUtil.getValue("restful.url.third.health.consultant.http"),
                        postStr);
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(responseMessage);
                
                logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                        + "返回结果的时间：" + DateUtil.now() + "返回结果内容："
                        + responseMessage);
                //立即返回客户端，并关闭连接
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
            
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + StringUtil.replaceSensInfo(postStr));
            
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //            //当前时间毫秒数
            //            Long currentTime = new Date().getTime();
            //            temporaryObject.setWaitTime(currentTime);
            //            //根据序列号放置结果
            //
            //            _msgSeq = (String) checkMap.get("msgSeq");
            //            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder();
            
            //设置AUTH
            headerMsg.append("isThirdAuth=\"true\"");
            //headerMsg.append(",userID=\"" + userID + "\"");
            
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTaskThird(
                    postStr, headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            //6.1.删除内存中的去掉的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    
                    String dateNow = DateUtil.now();
                    //超时返回报文
                    String timeoutResponse = "{\"partnerCode\":"
                            + (String) checkMap.get("partnerCode")
                            + ",\"serviceCode\":"
                            + (String) checkMap.get("serviceCode")
                            + ",\"version\":"
                            + (String) checkMap.get("version")
                            + " ,\"respondTime\":"
                            + dateNow
                            + ",\"msgType\": \"COMMON\" ,\"msgSeq\" :1,\"errorCode\":5,\"description\":\"请求超时\"}";
                    
                    response.getWriter().write(timeoutResponse);
                    
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + StringUtil.replaceSensInfo(responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"respondTime\":dateNow,\"msgType\": \"COMMON\" ,\"msgSeq\" :1,\"errorCode\":1,\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 支付宝支付异步通知接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/aliPayNotify")
    public void aliPayNotify(HttpServletRequest request,
            HttpServletResponse response)
    {
        try
        {
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            logger.info("收到支付宝异步通知信息：" + StringUtil.mapToJson(requestParams));
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();)
            {
                String name = iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++)
                {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //valueStr = new String(valueStr.getBytes("ISO-8895-1"), "UTF-8");
                params.put(name, valueStr);
            }
            //RSA解密
            //if (AlipayConfig.sign_type.equals("RSA"))
            //{
            //   params = AlipayNotify.decrypt(params, "UTF-8");
            //}
            //验证是否支付宝发出的合法信息
            boolean isAliPayInfo = AlipayNotify.verify(params);
            if (isAliPayInfo)
            {
                //调用/noauth/serviceManager接口,进行业务处理
                String url = PropertyUtil.getValue("alipay.notify.noauth.nio.http");
                if (StringUtil.isEmpty(url))
                {
                    url = "http://localhost:8080/noauth/serviceManager";
                }
                params.put("msgType", "MSG_ALIPAY_REQ");
                params.put("version", "0x0010");
                params.put("msgSeq", "1");
                String responseStr = HttpClientUtils.healthHttpPost(url,
                        StringUtil.mapToJson(params));
                //解析返回值
                Map<String, Object> returnMap = StringUtil.jsonToMap(responseStr);
                if ("0".equals((String) returnMap.get("errorCode")))
                {
                    logger.info("返回给支付宝：" + "success");
                    response.setContentType("text/html;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("success");
                }
                else
                {
                    logger.info("返回给支付宝：" + "fail");
                    response.setContentType("text/html;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("fail");
                }
                
            }
        }
        catch (Exception e)
        {
            logger.error("支付宝异步通知接口出错：", e);
            try
            {
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("fail");
            }
            catch (IOException e1)
            {
                logger.error("支付宝异步通知接口出错：", e1);
            }
        }
    }
    
    /**
     * 支付宝退款异步通知接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/aliReFundNotify")
    public void aliReFundNotify(HttpServletRequest request,
            HttpServletResponse response)
    {
        try
        {
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            logger.info("收到支付宝退款异步通知信息：" + StringUtil.mapToJson(requestParams));
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();)
            {
                String name = iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++)
                {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //valueStr = new String(valueStr.getBytes("ISO-8895-1"), "UTF-8");
                params.put(name, valueStr);
            }
            //RSA解密
            //if (AlipayConfig.sign_type.equals("RSA"))
            //{
            //   params = AlipayNotify.decrypt(params, "UTF-8");
            //}
            //验证是否支付宝发出的合法信息
            boolean isAliPayInfo = com.cmcciot.mat.common.utils.alipayrefund.alipay.util.AlipayNotify.verify(params);
            if (isAliPayInfo)
            {
                //调用/noauth/serviceManager接口,进行业务处理
                String url = PropertyUtil.getValue("alipay.notify.noauth.nio.http");
                if (StringUtil.isEmpty(url))
                {
                    url = "http://localhost:8080/noauth/serviceManager";
                }
                params.put("msgType", "MSG_ALIPAY_REFUND_REQ");
                params.put("version", "0x0010");
                params.put("msgSeq", "1");
                String responseStr = HttpClientUtils.healthHttpPost(url,
                        StringUtil.mapToJson(params));
                //解析返回值
                Map<String, Object> returnMap = StringUtil.jsonToMap(responseStr);
                if ("0".equals((String) returnMap.get("errorCode")))
                {
                    response.setContentType("text/html;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("success");
                }
                else
                {
                    response.setContentType("text/html;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("fail");
                }
                
            }
        }
        catch (Exception e)
        {
            logger.error("支付宝异步通知接口出错：", e);
            try
            {
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("fail");
            }
            catch (IOException e1)
            {
                logger.error("支付宝异步通知接口出错：", e1);
            }
        }
    }
    
    /**
     * <调用支付宝退费接口的接口>
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/nio/alipayReFundRequset")
    public void alipayReFundRequset(HttpServletRequest request,
            HttpServletResponse response)
    {
        String ip = IpTools.getIpAddr(request);
        logger.info("支付宝退费接口：远程请求地址是：" + ip);
        String[] ipArray = PropertyUtil.getValue4Array("http.ip.white");
        List<String> listArray = Arrays.asList(ipArray);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        //判断IP是否是服务管理内网IP
        boolean bIp = listArray.contains(ip);
        try
        {
            if (!bIp)
            {//未在配置中找到IP白名单
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .write("{\"errorCode\":\"9\",\"description\":\"非法来源地址！\"}");
                return;
            }
            
            StringBuffer buffer = new StringBuffer();
            InputStream in;
            
            in = request.getInputStream();
            /*BufferedInputStream bis = new BufferedInputStream(in);
            byte[] bt = new byte[1024];
            int iRead;
            while ((iRead = bis.read(bt)) != -1)
            {
                buffer.append(new String(bt, 0, iRead, "UTF-8"));
            }

            //接口参数
            String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            logger.info("支付宝退费接口：ip" + ip + "参数：" + postStr);
            Map<String, Object> map = new HashMap<String, Object>();
            map = StringUtil.jsonToMap(postStr);
            
            //服务器异步通知页面路径
            String notify_url = PropertyUtil.getValue("alipay_refund_notify_url");
            //需http://格式的完整路径，不允许加?id=123这类自定义参数
            
            //卖家支付宝帐户
            String seller_email = (String) map.get("WIDseller_email");
            //必填
            
            //退款当天日期
            String refund_date = (String) map.get("WIDrefund_date");
            //必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01 13:13:13
            
            //批次号 ((String)map.get("WIDdetail_data"))
            String batch_no = (String) map.get("WIDbatch_no");
            //必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001
            
            //退款笔数
            String batch_num = (String) map.get("WIDbatch_num");
            //必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）
            
            //退款详细数据
            String detail_data = (String) map.get("WIDdetail_data");
            //必填，具体格式请参见接口技术文档
            
            //////////////////////////////////////////////////////////////////////////////////
            
            //把请求参数打包成数组
            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("service", "refund_fastpay_by_platform_nopwd");
            sParaTemp.put("partner",
                    com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.partner);
            sParaTemp.put("_input_charset",
                    com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.input_charset);
            sParaTemp.put("notify_url", notify_url);
            sParaTemp.put("seller_email", seller_email);
            sParaTemp.put("refund_date", refund_date);
            sParaTemp.put("batch_no", batch_no);
            sParaTemp.put("batch_num", batch_num);
            sParaTemp.put("detail_data", detail_data);
            sParaTemp.put("seller_user_id",
                    com.cmcciot.mat.common.utils.alipayrefund.alipay.config.AlipayConfig.partner);
            
            //建立请求
            String sHtmlText = AlipaySubmit.buildRequest("", "", sParaTemp);
            logger.info("支付宝退费返回信息：" + sHtmlText);
            
            returnMap.put("returnText", sHtmlText);
            returnMap.put("errorCode", "0");
            
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
            returnMap.put("errorCode", "1");
        }
        finally
        {
            try
            {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(StringUtil.mapToJson(returnMap));
            }
            catch (IOException e)
            {
            	logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 健康模块-地址转发
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/nio/cyTest")
    public void cyTest(HttpServletRequest request, HttpServletResponse response)
    {
        String responseContent = null;
        try
        {
            
            //获取用户信息
            responseContent = "{\"version\":16,\"errorCode\":\"0\",\"description\":\"success\"}";
            
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
        finally
        {
            
            try
            {
                response.getWriter().write(responseContent);
            }
            catch (IOException e)
            {
            	logger.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 非鉴权接口-boss前置机
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/noauth/bossServiceManager")
    public void noAuthBossServiceManagerNio(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值
            StringBuffer buffer = new StringBuffer();
            InputStream in = request.getInputStream();
            /*BufferedInputStream bis = new BufferedInputStream(in);
            byte[] bt = new byte[1024];
            int iRead;
            while ((iRead = bis.read(bt)) != -1)
            {
                buffer.append(new String(bt, 0, iRead, "UTF-8"));
            }
            //接口参数
            String postStr = buffer.toString();*/
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            //   logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
            //         + StringUtil.replaceSensInfo(postStr));
            
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //postStr从xml转换成json
            
            //   String postJson = JacksonUtil.xmlToJson(postStr);
            //4.建立子线程，用于向服务管理传递参数
            
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTaskBoss(
                    postStr, headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                    
                    //转化成json
                    //timeoutResponse = JacksonUtil.jsonToXml(timeoutResponse);
                    response.getWriter().write(timeoutResponse);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容：" + (responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 安沃厂商推广iosapp用户点击信息通知接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.GET, value = "/appAWSpreadNotify")
    public void spreadNotifyAW(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值   获取app推广商通知过来的用户ios安装信息
            String idfa = request.getParameter("IDFA");
            
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + "IDFA=" + idfa);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("IDFA", idfa);
            
            map.put("msgType", "MSG_APP_SPREAD_REQ");
            //区分推广厂商的字段参数,根据客户端接入协议说明
            map.put("MIDCODE", "1001");
            String postStr = StringUtil.mapToJson(map);
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(postStr,
                    headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                    
                    response.getWriter().write(timeoutResponse);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + StringUtil.replaceSensInfo(responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 有米厂商推广iosapp用户点击信息通知接口
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.GET, value = "/appYMSpreadNotify")
    public void spreadNotifyYM(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值   获取app推广商通知过来的用户ios安装信息
            String idfa = request.getParameter("IDFA");
            String callback_url = request.getParameter("CALLBACK_URL");
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + "IDFA=" + idfa + "CALLBACK_URL=" + callback_url);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("IDFA", idfa);
            map.put("CALLBACK_URL", callback_url);
            map.put("msgType", "MSG_APP_SPREAD_REQ");
            //区分推广厂商的字段参数,根据客户端接入协议说明
            map.put("MIDCODE", "1002");
            String postStr = StringUtil.mapToJson(map);
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(postStr,
                    headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                    
                    response.getWriter().write(timeoutResponse);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + StringUtil.replaceSensInfo(responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 接口状态管理
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.GET, value = "/apiChangeState")
    public void apiChangeState(HttpServletRequest request,
            HttpServletResponse response)
    {
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值   获取app推广商通知过来的用户ios安装信息
            String api_key = request.getParameter("apikey");
            String api_value = request.getParameter("value");
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + "api_key=" + api_key + "api_value=" + api_value);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("APIKEY", api_key);
            map.put("APIVALUE", api_value);
            map.put("msgType", "MSG_API_CHANGE_STATE_REQ");
            
            String postStr = StringUtil.mapToJson(map);
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //4.建立子线程，用于向服务管理传递参数
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(postStr,
                    headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"请求超时\"}";
                    
                    response.getWriter().write(timeoutResponse);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    response.getWriter().write(responseVal);
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + StringUtil.replaceSensInfo(responseVal));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "，错误内容：" + e);
            //统一错误
            String errorResponse = "{\"version\":\"16\",\"msgType\":\"COMMON\",\"msgSeq\":\"0\",\"errorCode\":\"1\",\"description\":\"服务器内部错误\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 三诺同步设备信息
     * <功能详细描述>
     *
     * @param request
     * @param response [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sannuoSyncDeviceInfo")
    public void sannuoSyncDeviceInfo(HttpServletRequest request,
            HttpServletResponse response)
    {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        
        //1.按照规定格式组装后的msgSeq(不能用内存中的msgSeq,因为传递时，值可能会变)
        String _msgSeq = "";
        //2.2.根据消息队列获取路由器返回的值,用于回执给APP
        String responseVal = "";
        //数据临时存放地，用于唤醒对象
        TemporaryObject temporaryObject = new TemporaryObject();
        try
        {
            //1.获取参数值
            StringBuffer buffer = new StringBuffer();
            InputStream in = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            String postStr = buffer.toString();
            reader.close();//关闭数据流
            logger.info("远程请求地址：" + IpTools.getIpAddr(request) + "，请求参数："
                    + StringUtil.replaceSensInfo(postStr));
            
            //2.1.生成全站唯一消息序列(线程安全+序列号)
            synchronized (lockMsgSeq)
            {
                //序列号+1
                Integer nMsgSeq = lockMsgSeq.getnMsgSeq() + 1;
                
                //配置文件ID + 消息序列,不足8位，前面以0补充
                final String STR_FORMAT = "00000000";
                DecimalFormat df = new DecimalFormat(STR_FORMAT);
                _msgSeq = PropertyUtil.getValue("http.service.id")
                        + df.format(nMsgSeq);
                
                //如果大于8位数
                if (nMsgSeq >= 99999999)
                {//从新归零计数
                    lockMsgSeq.setnMsgSeq(0);
                }
                else
                {
                    lockMsgSeq.setnMsgSeq(nMsgSeq);
                }
            }
            //当前时间毫秒数
            Long currentTime = new Date().getTime();
            temporaryObject.setWaitTime(currentTime);
            //根据序列号放置结果
            HttpKeyConstant.MEMORYVALUE.put(_msgSeq, temporaryObject);
            
            //3.放置header信息
            StringBuilder headerMsg = new StringBuilder("isAuth=\"false\"");
            //设置KEY
            headerMsg.append(",key=\""
                    + KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                            + PropertyUtil.getValue("http.service.password"))
                    + "\"");
            //设置serverID
            headerMsg.append(",serverID=\""
                    + PropertyUtil.getValue("http.service.id") + "\"");
            //设置SEQ(不能用内存中的msgSeq,因为传递时，值可能会变)
            headerMsg.append(",msgSeq=\"" + _msgSeq + "\"");
            
            //参数添加msgType
            Map<String, Object> paramMap = StringUtil.jsonToMap(postStr);
            if (null != paramMap.get("jsonError"))
            {
                returnMap.put("statusCode", "02");
                returnMap.put("desc", "参数错误!");
                response.getWriter().write(StringUtil.mapToJson(returnMap));
                String dateNow = DateUtil.now();
                logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                        + "返回结果的时间：" + dateNow + "返回结果内容："
                        + StringUtil.mapToJson(returnMap));
            }
            paramMap.put("msgType", "MSG_SANNUO_SYNC_DEVICEINFO_REQ");
            paramMap.put("version", 0X0014);
            paramMap.put("msgSeq", 0);
            //4.建立子线程，用于向服务管理传递参数
            
            HttpKeyConstant.PRODUCERPOOL.execute(new ThreadPoolTask(
                    StringUtil.mapToJson(paramMap), headerMsg.toString()));
            
            //5.锁定消息序列号对应的临时对象（该对象处理服务管理所主动传递的值）
            synchronized (temporaryObject)
            {
                try
                {
                    temporaryObject.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                            + "，错误内容：" + e);
                }
            }
            responseVal = temporaryObject.getResponseVal();
            
            //6.1.删除内存中的使用过的数据
            HttpKeyConstant.MEMORYVALUE.remove(_msgSeq);
            //6.2.向app返回值
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try
            {
                if (StringUtil.isEmpty(responseVal))
                {
                    //超时返回报文
                    String timeoutResponse = "{\"statusCode\":\"03\",\"desc\":\"服务器异常\"}";
                    response.getWriter().write(timeoutResponse);
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + timeoutResponse);
                }
                else
                {
                    Map<String, Object> resMap = StringUtil.jsonToMap(responseVal);
                    resMap.remove("msgSeq");
                    resMap.remove("msgType");
                    resMap.remove("version");
                    response.getWriter().write(StringUtil.mapToJson(resMap));
                    
                    String dateNow = DateUtil.now();
                    logger.info("返回结果的远程地址：" + IpTools.getIpAddr(request)
                            + "返回结果的时间：" + dateNow + "返回结果内容："
                            + (StringUtil.mapToJson(resMap)));
                }
            }
            catch (IOException e)
            {
                logger.error("远程请求地址：" + IpTools.getIpAddr(request)
                        + "，返回时结果时错误:" + e);
            }
        }
        catch (Exception e)
        {
            logger.error("远程请求地址：" + IpTools.getIpAddr(request) + "报错", e);
            //统一错误
            String errorResponse = "{\"statusCode\":\"03\",\"desc\":\"服务器异常\"}";
            try
            {
                response.getWriter().write(errorResponse);
            }
            catch (IOException e1)
            {
                logger.error("返回消息报错", e);
            }
        }
        finally
        {
            //立即返回客户端，并关闭连接
            try
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            catch (IOException e)
            {
                logger.error("关闭输出流错误", e);
            }
            
        }
    }
}
