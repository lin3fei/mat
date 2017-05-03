package com.cmcciot.mat.appagent.http.nio.threadpool;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcciot.mat.common.utils.HttpClientUtils;
import com.cmcciot.mat.common.utils.PropertyUtil;

public class ThreadPoolTask implements Runnable, Serializable
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
    
    public ThreadPoolTask()
    {
        
    }
    
    public ThreadPoolTask(String postMsg, String headerMsg)
    {
        this.postMsg = postMsg;
        this.headerMsg = headerMsg;
    }
    
    // 每个任务的执行过程，现在是什么都没做，除了print和sleep)
    @Override
    public void run()
    {
        //同时支持两套（http和https）
        //        String httpType = HttpKeyConstant.HTTPTYPES.get(sessionID);
        //        if (httpType.equals("http"))
        //        {
        //            HttpClientUtils.sendHttpPostRequest(PropertyUtil.getValue("restful.url.auth.nio.http"),
        //                    postMsg,
        //                    headerMsg);
        //        }
        //        else if (httpType.equals("https"))
        //        {
        //            HttpClientUtils.sendSSLPostRequest(PropertyUtil.getValue("restful.url.auth.nio.https"),
        //                    postMsg,
        //                    headerMsg);
        //        }
    	//String platformUrl = PropertyUtil.getValue("restful.url.auth.nio.http");
    	logger.debug("send request to platform : " + PropertyUtil.getValue("restful.url.auth.nio.http"));
        HttpClientUtils.sendHttpPostRequest(PropertyUtil.getValue("restful.url.auth.nio.http"),
                postMsg,
                headerMsg);
    }
}
