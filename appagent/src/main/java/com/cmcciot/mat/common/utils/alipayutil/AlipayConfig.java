package com.cmcciot.mat.common.utils.alipayutil;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2015-0-19
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
    
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
    
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    public static String partner = "2088511393371193";
    // 商户的私钥
    public static String private_key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMvG7XFKmrgLhMmvledg0/mPthdisDWrUqc9+apFUN4YdUi6+ITbmzD51aS0+8QFr0RIKzfB+GrbyJ2N/sGq+KiA8yF1xMb+VpdsgeeQ6b0BXlhSjctlDfDwGDT5jLryKVuSrCuLXH9E2suAFVxAHB8XUOQCkGcsPifhsi4+zDHRAgMBAAECgYAW3oVpUIEapJhLdjwxkmlXuawq5bV24HpPMKJjXJIdysrm1azsZCT/kxpJ+NHEhwrLc/n9sPQrMe1K2VO88m4jb4U+GkDF5xW7y55HZwnIUSXkWDov+LBstpMHxzdBSfpZuyny/feSJUjhqUmzjDZ4ziCx1UerloxiionzDcV5DQJBAPzcl/enICMuZLmQ/4bnQcFDRf2I7FVcRHt/TtVUCfN4TwOObAfD67TmfDwysjgX0I8OKvRH+LUljfGyalyjracCQQDOTmEii36x0eH4lfT52mg2vE/UopJRXBIxtWSebNBHEPw3jgcUIHTDiUhDYq/zjND3GeWBF1v5uZO+oHHspsPHAkBiDRQspzTSD5plTrGXWTNpQrHwN1kVXJr9nIcwN89IBt30zhRcCn/q/PktyqbLE4j/AApuAT6S18Yr8/hEhf+/AkBqMUIii/ctBMHyOQ4eReP6uLfG1d/G7UOYPqyKN6yFyCJrXjoajBJOTC3cMcIr9UHiz3upFSMMyg9YwjGiBL0lAkAm+BQQDwaWDvfqWdLo9F8hLZjA50TNx8qUYEAhsJpPA9D65g84GqY6CQ0Te9eS3Rm6xWN+pbb29aVzDAi1nMMI";
    
    // 支付宝的公钥，无需修改该值
    public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    //↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    

    // 调试用，创建TXT日志文件夹路径
    public static String log_path = "D:\\";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";
    
    // 签名方式 不需修改
    public static String sign_type = "RSA";

}
