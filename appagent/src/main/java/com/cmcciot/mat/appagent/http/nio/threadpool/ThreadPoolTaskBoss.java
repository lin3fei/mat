package com.cmcciot.mat.appagent.http.nio.threadpool;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.common.utils.HttpClientUtils;
import com.cmcciot.mat.common.utils.PropertyUtil;

public class ThreadPoolTaskBoss implements Runnable, Serializable
{
    
    /**
     * JDK1.5中，每个实现Serializable接口的类都推荐声明这样的一个ID
     */
    private static final long serialVersionUID = 1L;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * 消息内容
     */
    private String postMsg;
    
    /**
     * 头部内容
     */
    private String headerMsg;
    
    public ThreadPoolTaskBoss()
    {
        
    }
    
    public ThreadPoolTaskBoss(String postMsg, String headerMsg)
    {
        this.postMsg = postMsg;
        this.headerMsg = headerMsg;
    }
    
    // 每个任务的执行过程，现在是什么都没做，除了print和sleep)
    @Override
    public void run()
    {
       String requesUrl = PropertyUtil.getValue("boss.url.noauth.nio.http");
        
    	logger.debug("send request to platform : " + requesUrl);
        HttpClientUtils.sendHttpPostRequest(requesUrl,
                postMsg,
                headerMsg);
    }
}
