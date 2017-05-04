package com.cmcciot.mat.elecalarm.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;

public class ElecAlarmConstant
{
    /**
     * 执行线程消息寄存器
     */
    public static ConcurrentHashMap<String, Object> threadMemoryMap = new ConcurrentHashMap<String, Object>();
    
    /**
     * 围栏信息消息寄存器
     */
    public static ConcurrentHashMap<String, Object> elecAlarmMemoryMap = new ConcurrentHashMap<String, Object>();
    
    /**
     * 核心线程数
     */
    public static int CORE_POOL_SIZE = Integer.parseInt(PropertyUtil.getValue("threadPool.corepool_size") == null ? "10"
            : PropertyUtil.getValue("threadPool.corepool_size"));
    
    /**
     * 线程最大容量
     */
    public static int MAXIMUM_POOL_SIZE = Integer.parseInt(PropertyUtil.getValue("threadPool.maximum_pool_size") == null ? "500"
            : PropertyUtil.getValue("threadPool.maximum_pool_size"));
    
    /**
     * 缓存队列容量
     */
    public static int BLOCKING_QUEUE = Integer.parseInt(PropertyUtil.getValue("threadPool.blocking_queue") == null ? "1000"
            : PropertyUtil.getValue("threadPool.blocking_queue"));
    
    /**
     * 构造一个线程池 ,用于调用云端发送短信
     */
    public static ThreadPoolExecutor PRODUCERPOOL = new ThreadPoolExecutor(
            ElecAlarmConstant.CORE_POOL_SIZE,
            ElecAlarmConstant.MAXIMUM_POOL_SIZE,
            180,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(ElecAlarmConstant.BLOCKING_QUEUE),
            new ThreadPoolExecutor.DiscardOldestPolicy());
}
