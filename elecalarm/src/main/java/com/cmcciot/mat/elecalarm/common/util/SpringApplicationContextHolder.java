package com.cmcciot.mat.elecalarm.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContextHolder implements ApplicationContextAware
{
    private static ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException
    {
        SpringApplicationContextHolder.context = context;
        
    }
    
    public static ApplicationContext getApplicationContext()
    {
        return context;
    }
    
    /*** 
     * 根据一个bean的id获取配置文件中相应的bean 
     * @param name 
     * @return 
     * @throws BeansException 
     */
    public static Object getBean(String name) throws BeansException
    {
        return context.getBean(name);
    }
    
}
