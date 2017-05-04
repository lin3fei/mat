package com.cmcciot.mat.elecalarm.common.exception;

public interface ErrorNumber
{
    /**
     * 数据库内部错误
     */
    public int DATABASE_ERROR = 1;
    
    /**
     * 空指针错误
     */
    public int NULL_ERROR = 2;
    
    /**
     * 参数错误
     */
    public int PARAMETER_ERROR = 3;
    
    /**
     * 格式错误
     */
    public int FORMAT_ERROR = 4;
    
    
    /**
     * 调用接口异常
     */    
    public int ERROR_INTERFACE = 10;
    
    /**
     * 数据转换错误
     */    
    public int ERROR_JSON_TO_OBJECT = 11;

    public int ERROR_OBJECET_TO_JSON = 12;
    
    public int ERROR_NOT_ALLOW = 13;
    
    
}
