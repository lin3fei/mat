/*
 * 文 件 名:  HttpClientUtils.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年7月25日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author Administrator
 * @version [版本号, 2014年7月25日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class HttpClientUtils {
    
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    
    /**
     * 发送HTTPS请求
     * <功能详细描述>
     *
     * @param reqURL
     * @param message
     * @param headMessage
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String sendSSLPostRequest(String reqURL,
                                            final String postMsg, String headerMsg) {
        long responseLength = 0; //响应长度 
        String responseContent = null; //响应内容 
        HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例 
        X509TrustManager xtm = new X509TrustManager() { //创建TrustManager
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        try {
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext 
            SSLContext ctx = SSLContext.getInstance("TLS");

            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用 
            ctx.init(null, new TrustManager[]{xtm}, null);

            //创建SSLSocketFactory 
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上 
            httpClient.getConnectionManager()
                    .getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));

            HttpPost httpPost = new HttpPost(reqURL); //创建HttpPost 
            //            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //构建POST请求的表单参数 
            //            for (Map.Entry<String, String> entry : params.entrySet())
            //            {
            //                formParams.add(new BasicNameValuePair(entry.getKey(),
            //                        entry.getValue()));
            //            }
            //            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));

            ContentProducer cp = new ContentProducer() {
                @Override
                public void writeTo(OutputStream outstream) throws IOException {
                    //2.2.传递app所请求的参数
                    Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                    writer.write(postMsg);
                    writer.flush();
                }
            };
            httpPost.setEntity(new EntityTemplate(cp));

            if (headerMsg != null) {
                httpPost.setHeader("HOA_auth", headerMsg);
            }
            HttpResponse response = httpClient.execute(httpPost); //执行POST请求 
            HttpEntity entity = response.getEntity(); //获取响应实体 

            if (null != entity) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity); //Consume response content 
            }
            System.out.println("请求地址: " + httpPost.getURI());
            System.out.println("响应状态: " + response.getStatusLine());
            System.out.println("响应长度: " + responseLength);
            System.out.println("响应内容: " + responseContent);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }
        return responseContent;
    }

    /**
     * 发送HTTP请求
     * <功能详细描述>
     *
     * @param reqURL
     * @param message
     * @param headMessage
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String sendHttpPostRequest(String reqURL,
                                             final String postMsg, String headerMsg) {
        HttpClient client = new DefaultHttpClient();
        //根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost(reqURL);
        ContentProducer cp = new ContentProducer() {
            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                //传递app所请求的参数
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(postMsg);
                writer.flush();
            }
        };
        post.setEntity(new EntityTemplate(cp));
        post.setHeader("HOA_auth", headerMsg);

        //2.3.执行http请求
        HttpResponse httpResponse;
        String responseContent = "";
        try {
            //            logger.debug("远程地址：" + PropertyUtil.getValue("restful.url.auth.nio")
            //                    + "请求时间：" + DateUtil.now());
            httpResponse = client.execute(post);
            //3.获取服务管理返回值
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream instreams = entity.getContent();
                responseContent = StringUtil.convertStreamToString(instreams);
                //                logger.debug("子线程响应地址："
                //                        + PropertyUtil.getValue("restful.url.auth.nio")
                //                        + "，子线程响应结果：" + state);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            //            logger.error("错误结果：" + e);
        } catch (IOException e) {
            e.printStackTrace();
            //            logger.error("错误结果：" + e);
        } finally {
            post.releaseConnection();
            client.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }

        return responseContent;
    }

    /**
     * <微博Post方法>
     * <功能详细描述>
     *
     * @param code
     * @param params
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String weiboHttpPost(String code, PostParameter[] params) {
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        String url = OautherPropertyUtil.getValue("weibo_accessTokenURL");
        if (StringUtil.isEmpty(url)) {
            url = "https://api.weibo.com/oauth2/access_token";
        }
        PostMethod postMethod = new PostMethod(url);
        for (int i = 0; i < params.length; i++) {
            postMethod.addParameter(params[i].getName(), params[i].getValue());
        }
        HttpMethodParams param = postMethod.getParams();
        param.setContentCharset("UTF-8");

        int responseCode = -1;
        try {

            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            client.executeMethod(postMethod);
            responseCode = postMethod.getStatusCode();
            logger.info("微博登录发送请求接口，地址：" + url +",请求参数：" + param + ",返回状态码：" + responseCode);
            if (responseCode != 200) {
                return "";
            }
            return postMethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            logger.error("微博登录接口报错！",ioe);
            return "";
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * <微博GET方法>
     * <功能详细描述>
     *
     * @param params
     * @param token
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String weiboHttpGet(PostParameter[] params, String token) {
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        String url = OautherPropertyUtil.getValue("weibo_userinfo_uri");
        if (StringUtil.isEmpty(url)) {
            url = "https://api.weibo.com/2/users/show.json";
        }
        if (null != params && params.length > 0) {
            StringBuffer buf = new StringBuffer();
            for (int j = 0; j < params.length; j++) {
                if (j != 0) {
                    buf.append("&");
                }
                try {
                    buf.append(URLEncoder.encode(params[j].getName(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(params[j].getValue(),
                                    "UTF-8"));
                } catch (java.io.UnsupportedEncodingException neverHappen) {
                }
            }

            String encodedParams = buf.toString();
            if (-1 == url.indexOf("?")) {
                url += "?" + encodedParams;
            } else {
                url += "&" + encodedParams;
            }
        }
        GetMethod getmethod = new GetMethod(url);
        InetAddress ipaddr = null;
        int responseCode = -1;
        try {
            ipaddr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        if (token == null) {
            throw new IllegalStateException("Oauth2 token is not set!");
        }
        headers.add(new Header("Authorization", "OAuth2 " + token));
        headers.add(new Header("API-RemoteIP", ipaddr.getHostAddress()));
        client.getHostConfiguration()
                .getParams()
                .setParameter("http.default-headers", headers);

        try {

            getmethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            client.executeMethod(getmethod);
            responseCode = getmethod.getStatusCode();
            logger.info("微博登录发送请求接口，地址：" + url +",返回状态码：" + responseCode);
            if (responseCode != 200) {
                return "";
            }
            return getmethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            logger.error("微博登录接口报错！",ioe);
            return "";
        } finally {
            getmethod.releaseConnection();
        }
    }

    /**
     * <QQ登陆GET方法>
     * <功能详细描述>
     *
     * @param params
     * @param url
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String qqHttpGet(String url, PostParameter[] params) {
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        if (null != params && params.length > 0) {
            StringBuffer buf = new StringBuffer();
            for (int j = 0; j < params.length; j++) {
                if (j != 0) {
                    buf.append("&");
                }
                try {
                    buf.append(URLEncoder.encode(params[j].getName(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(params[j].getValue(),
                                    "UTF-8"));
                } catch (java.io.UnsupportedEncodingException neverHappen) {
                }
            }

            String encodedParams = buf.toString();
            if (-1 == url.indexOf("?")) {
                url += "?" + encodedParams;
            } else {
                url += "&" + encodedParams;
            }
        }
        GetMethod getmethod = new GetMethod(url);
        int responseCode = -1;
        try {

            getmethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            client.executeMethod(getmethod);
            responseCode = getmethod.getStatusCode();
            logger.info("QQ登录发送请求接口，地址：" + url +",返回状态码：" + responseCode);
            if (responseCode != 200) {
                return "";
            }
            return getmethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            logger.error("QQ登录接口报错！",ioe);
            return "";
        } finally {
            getmethod.releaseConnection();
        }
    }

    /**
     * <健康模块的接口转发Post方法>
     * <功能详细描述>
     *
     * @param params
     * @param url
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String healthHttpPost(String reqURL, final String postMsg) {
        HttpClient client = new DefaultHttpClient();
        //根据服务管理请求地址，进行http请求
        HttpPost post = new HttpPost(reqURL);
        ContentProducer cp = new ContentProducer() {
            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                //传递所请求的参数
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(postMsg);
                writer.flush();
            }
        };
        post.setEntity(new EntityTemplate(cp));

        //2.3.执行http请求
        HttpResponse httpResponse;
        String responseContent = "";
        try {

            httpResponse = client.execute(post);
            //3.获取服务管理返回值
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream instreams = entity.getContent();
                responseContent = StringUtil.convertStreamToString(instreams);

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
            client.getConnectionManager().shutdown(); //关闭连接,释放资源 
        }

        return responseContent;
    }

    /**
     * <健康模块的接口转发Get方法>
     */
    public static String healthHttpGet(String reqURL) {
        HttpClient client = new DefaultHttpClient();
        //根据服务管理请求地址，进行http请求
        HttpGet request = new HttpGet();


        //2.3.执行http请求
        HttpResponse httpResponse;
        String responseContent = "";
        try {

            request.setURI(new URI(reqURL));
            httpResponse = client.execute(request);

            //3.获取服务管理返回值
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream instreams = entity.getContent();
                responseContent = StringUtil.convertStreamToString(instreams);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            request.releaseConnection();
            client.getConnectionManager().shutdown(); //关闭连接,释放资源
        }

        return responseContent;
    }
    
    /**
     * 发送HTTP POST请求（Json数据）
     * @param url 请求地址
     * @param content 请求内容
     * @return
     * @throws IOException
     */  
    @SuppressWarnings("deprecation")
     public static String httpPostWithJson(String url, final String content)
             throws IOException
     {
         
         DefaultHttpClient httpClient = new DefaultHttpClient();       
         HttpPost httpPost = new HttpPost(url);        
         httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
		StringEntity se = new StringEntity(content, HTTP.UTF_8);        
         se.setContentType(CONTENT_TYPE_TEXT_JSON);       
         se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                 APPLICATION_JSON));       
         httpPost.setEntity(se);      
         HttpResponse response = httpClient.execute(httpPost);      
         String resp = EntityUtils.toString(response.getEntity());
         
         return resp;        
     }
}
