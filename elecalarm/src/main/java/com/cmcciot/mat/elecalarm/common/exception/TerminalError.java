package com.cmcciot.mat.elecalarm.common.exception;

public interface TerminalError
{
    /**
     * 成功
     */
    String SUCCESS = "succ";
    /**
     * 消息格式错误
     */
    String FORMAT_ERROR = "message format error";
    /**
     * 服务器内部错误
     */
    String SERVER_ERROR = "server error";
    /**
     * 设备未注册
     */
    String DEVICE_IS_NOT_REGISTER = "device is not register";
    /**
     * 设备未注册
     */
    String DEVICE_IS_UN_BAND = "device is unBand";
    /**
     * 设备未登录
     */
    String DEVICE_IS_NOT_LOGIN = "device is not login";
    /**
     * 设备不存在
     */
    String DEVICE_IS_NOT_EXIST = "device is not exist";
    
}
