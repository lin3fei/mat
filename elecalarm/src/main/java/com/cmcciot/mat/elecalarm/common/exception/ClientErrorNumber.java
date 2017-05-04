package com.cmcciot.mat.elecalarm.common.exception;

/**
 * 
 * <返回客户端错误码统一定义>
 * <功能详细描述>
 * 
 * @author  admin
 * @version  [版本号, 2014年12月5日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ClientErrorNumber
{
    /**
     * 成功
     */
    public int SUUCESS = 0x0000;
    
    /**
     * 服务器内部错误
     */
    public int SERVICE_ERROR = 0x0001;
    
    /**
     * 消息格式错误
     */
    public int PARAM_ERROR = 0x0002;
    
    /**
     * 请求参数非法
     */
    public int PARAM_ILLEGAL = 0x0003;
    
    /**
     * 无此操作权限
     */
    public int NO_PERMISSION = 0x0004;
    
    /**
     * 该手机已被注册
     */
    public int PHONE_NUM_HAS_REGISTERED = 0x1000;
    
    /**
     *该用户名已注册
     */
    public int USERNAME_HAS_REGISTERED = 0x1001;
    
    /**
     *用户名或密码错
     */
    public int USERNAME_OR_PASSWORD_ERROR = 0x1002;
    
    /**
     *旧密码不匹配
     */
    public int OLD_PASSWORD_ERROR = 0x1003;
    
    /**
     *用户帐号不存在
     */
    public int ACCOUNT_NOT_EXIST = 0x1004;
    
    /**
     *图片验证码错误 Image verification code error
     */
    public int IMAGE_CODE_ERROR = 0x1005;
    
    /**
     *用户名或密码连续错误3次以上
     */
    public int USERNMAE_OR_PASSWOR_THREE_ERROR = 0x1006;
    
    /**
     *登录缺少图片验证码
     */
    public int LOGGIN_NEED_IMAGE_CODE = 0x1007;
    
    /**
     *图片验证码超时 
     */
    public int IMAGE_CODE_OVERTIME = 0x1008;
    
    /**
     *短信验证码错误 
     */
    public int SMS_CODE_ERROR = 0x2000;
    
    /**
     *短信发送重复请求
     */
    public int SMS_SEND_REQ_REPEAT = 0x2001;
    
    /**
     *短信发送次数超限
     */
    public int SMS_SEND_OUT_OF_RANGE = 0x2003;
    
    /**
     *短信验证码超时
     */
    public int SMS_CODE_OVERTIME = 0x2004;
    
}
