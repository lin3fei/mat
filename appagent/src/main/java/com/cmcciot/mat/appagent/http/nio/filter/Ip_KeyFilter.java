package com.cmcciot.mat.appagent.http.nio.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmcciot.mat.common.utils.IpTools;
import com.cmcciot.mat.common.utils.KeyUtil;
import com.cmcciot.mat.common.utils.PropertyUtil;
import com.cmcciot.mat.common.utils.StringUtil;

public class Ip_KeyFilter implements Filter
{
    
    @Override
    public void destroy()
    {
    }
    
    /**
     * 验证来源IP是否为指定的IP
     * @param req
     * @param resp
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain filterChain) throws IOException, ServletException
    {
        //请求信息
        HttpServletRequest request = (HttpServletRequest) req;
        //获取来源IP
        String sourceIP = IpTools.getIpAddr(request);
        //取出配置的IP
        String[] ipArray = PropertyUtil.getValue4Array("http.ip.white");
        List<String> listArray = Arrays.asList(ipArray);
        //判断IP是否是服务管理内网IP
        boolean bIp = listArray.contains(sourceIP);
        if (!bIp)
        {//未在配置中找到IP白名单
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write("{\"errorCode\":\"9\",\"description\":\"非法来源地址！\"}");
            return;
        }
        
        //根据请求，取出header中的msgSeq的值
        String[] headerEntries = StringUtil.splitIgnoringQuotes(request.getHeader("HOA_auth"),
                ',');
        Map<String, String> headerMap = StringUtil.splitEachArrayElementAndCreateMap(headerEntries,
                "=",
                "\"");
        //头部序列号
        String key = headerMap.get("key");
        String localKey = KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id")
                + PropertyUtil.getValue("http.service.password"));
        if (!key.equals(localKey))
        {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write("{\"errorCode\":\"9\",\"description\":\"密匙不正确！\"}");
            return;
        }
        
        //验证成功，继续执行。
        filterChain.doFilter(request, resp);
        return;
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
    }
}
