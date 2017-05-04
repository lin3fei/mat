package com.cmcciot.mat.enclosure.common;

/**
 * 自定义日志异常
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  ywx167782
 * @version  [版本号, 2014年3月31日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class LogicException extends RuntimeException
{
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误分类
     */
    private int errorNumber;
    
    /**
     * 错误内容
     */
    private String errorMessage;
    
    public LogicException(int errorNumber, String errorMessage)
    {
        this.errorNumber = errorNumber;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String getMessage()
    {
        return errorMessage;
    }
    
    public int getErrorNumber()
    {
        return errorNumber;
    }
}
