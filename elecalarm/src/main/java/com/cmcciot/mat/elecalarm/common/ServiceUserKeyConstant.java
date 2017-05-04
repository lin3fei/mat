package com.cmcciot.mat.elecalarm.common;

import java.util.HashMap;
import java.util.Map;

public class ServiceUserKeyConstant
{
    /**
     * 返回成功
     */
    public static Integer ERRORCODE_SUCCESS = 0x0000;
    
    /**
     * 服务器内部错误
     */
    public static Integer ERRORCODE_SERVER_ERROR = 0x0001;
    
    /**
     * 消息格式错误
     */
    public static Integer ERRORCODE_FORMAT_ERROR = 0x0002;
    
    /**
     * 请求参数非法
     */
    public static Integer ERRORCODE_REQUEST_ERROR = 0x0003;
    
    /**
     * 该业务需要鉴权
     */
    public static Integer ERRORCODE_USERORPAS_ERROR = 0x0004;
    
    /**
     * 路由器不存在
     */
    //public static Integer ERRORCODE_SEVICEORINITIALIZE_ERROR = 0x3007;
    
    /**
     * 路由器未与用户绑定
     */
    //public static Integer ERRORCODE_ROUTERNOTBOUNDUSER_ERROR = 0x1006;
    
    /**
     * 路由器未上报属性，不能进行远程业务操作
     */
    //public static Integer ERRORCODE_ROUTERNOTABILITY_ERROR = 0x3001;
    
    /**
     * 路由器不在线
     */
    //public static Integer ERRORCODE_ROUTERNOTONLINE_ERROR = 0x3008;
    
    /**
     * 0x0010 
     */
    public static Integer VERSION_0X0010 = 0x0010;
    
    /**
     * 无此操作权限
     */
    public static Integer ERRORCODE_NOOPERATORAUTH_ERROR = 0x0004;
    
    /**
     * 存放用户token信息
     */
    public static Map<String, Object> TOKENSTORE = new HashMap<String, Object>();
}
