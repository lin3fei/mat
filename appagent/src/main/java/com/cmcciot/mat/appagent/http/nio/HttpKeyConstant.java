package com.cmcciot.mat.appagent.http.nio;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cmcciot.mat.common.utils.PropertyUtil;

public abstract class HttpKeyConstant
{
    /**
     * 消息寄存器
     */
    public static ConcurrentHashMap<String, Object> MEMORYVALUE = new ConcurrentHashMap<String, Object>();
    
    /**
     * 监听线程关闭
     */
    public static volatile boolean THREADFLAG = true;
    
    /**
     * 超时总数
     */
    public static StringBuilder TIMEEOUTCOUNT = new StringBuilder("0");
    
    /**
     * 远程客户端对象
     */
    //public static HttpClient CLIENT = new DefaultHttpClient();
    
    /**
     * APP请求超时时间
     */
    public static long HTTP_TIMEOUT = Long.parseLong(PropertyUtil.getValue("restful.http.timeout"));
    
    /**
     * 核心线程数
     */
    public static int CORE_POOL_SIZE = Integer.parseInt(PropertyUtil.getValue("restful.threadPool.corepool_size"));
    
    /**
     * 线程最大容量
     */
    public static int MAXIMUM_POOL_SIZE = Integer.parseInt(PropertyUtil.getValue("restful.threadPool.maximum_pool_size"));
    
    /**
     * 缓存队列容量
     */
    public static int BLOCKING_QUEUE = Integer.parseInt(PropertyUtil.getValue("restful.threadPool.blocking_queue"));
    
    /**
     * 构造一个线程池 ,用于透传信息给服务管理的子线程集合
     */
    public static ThreadPoolExecutor PRODUCERPOOL = new ThreadPoolExecutor(200,
            HttpKeyConstant.MAXIMUM_POOL_SIZE, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(HttpKeyConstant.BLOCKING_QUEUE),
            new ThreadPoolExecutor.DiscardOldestPolicy());
}
