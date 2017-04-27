package com.cmcciot.mat.pushapi.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.cmcciot.mat.pushapi.sms.util.Util;

public class URLInvoke
{

	public static String post(String url, String msg)
	{
		String content = null;
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost postMethod = new HttpPost(url);
		try
		{
			// 从接过过来的代码转换为UTF-8的编码
			HttpEntity stringEntity = new StringEntity(msg, "UTF-8");
			postMethod.setEntity(stringEntity);
			HttpResponse response = httpClient.execute(postMethod);
			HttpEntity entity = response.getEntity();

			if (entity != null)
			{
				// 使用EntityUtils的toString方法，传递默认编码，在EntityUtils中的默认编码是ISO-8859-1
				content = EntityUtils.toString(entity, "UTF-8");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			postMethod.abort();
			httpClient.getConnectionManager().shutdown();
		}
		return content;
	}

	public static String get(String url)
	{
		String content = "Connect failed.";

		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 15000);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setHttpRequestRetryHandler(myRetryHandler);//设置重试
		HttpGet httpGet = new HttpGet(url);
		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null)
			{
				// 使用EntityUtils的toString方法，传递默认编码，在EntityUtils中的默认编码是ISO-8859-1
				content = EntityUtils.toString(entity, "UTF-8");
			}
			else
			{
				content = "Response is null";
			}
			if (!"{\"status\": 0, \"msg\": \"send successfully\"}".equals(content)) //服务端正确的返回内容
			{
				Util.log.warn("invoke [" + url + "] failed. response: [" + content + "]");
			}
			else
			{
				Util.log.debug("invoke [" + url + "] success. response: [" + content + "]");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Util.log.error("invoke [" + url + "] error, \n for the reason [" + e.getLocalizedMessage() + "]");
		}
		finally
		{
			httpGet.abort();
			httpClient.getConnectionManager().closeExpiredConnections();
		}
		return content;
	}

	public static String postWithParams(String postUrl,String postData)
	{
		try
		{
			// 发送POST请求
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			// Post 请求不能使用缓存
			conn.setUseCaches(true);
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			conn.setDoOutput(true);
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// System.out.println(postData);
			out.write(postData);
			out.flush();
			out.close();

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				System.out.println("connect failed!");
				return "";
			}
			// 获取响应内容体
			String line, result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));
			while ((line = in.readLine()) != null)
			{
				result += line + "\n";
			}
			in.close();
			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace(System.out);
		}
		return "";
	}

	static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler()
	{

		public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
		{
			if (executionCount >= 5)
			{
				System.out.println("request times=" + executionCount);
				return false;
			}
			if (exception instanceof InterruptedIOException)
			{
				// Timeout
				return false;
			}
			if (exception instanceof UnknownHostException)
			{
				// Unknown host
				return false;
			}
			if (exception instanceof ConnectException)
			{
				// Connection refused
				return false;
			}
			if (exception instanceof SSLException)
			{
				// SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
			if (idempotent)
			{
				// Retry if the request is considered idempotent 
				return true;
			}
			return false;
		}

	};
}
