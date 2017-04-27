package com.cmcciot.mat.pushapi.sms.util;

import java.util.ArrayList;
import java.util.List;

public class Consts
{
	//短信网关异常状态
	public static boolean smsExceptionStatus = false;

	//服务启动时间
	public static String upTime = "";

	
	/**
	 * 企信通账号
	 */
	//短信服务器地址
	public static String smsServerIp = "202.102.41.101";
	//短信服务器地址端口
	public static int smsServerPort = 9005;
	//短信服务器用户
	public static String accessUserName = "025C00297896";
	//短信服务器密码
	public static String accessPassword = "123456ab";
	
	/**
	 * 易达平台
	 */
	//易达请求地址
	public static String ydurl="";
	//账户
	public static String sname="";
	//账户密码
	public static String spwd="";
	//企业代码
	public static String scorpid="";
	

	//短信发送号码黑名单列表
	public static String blackList = "";
	
	//短信服务供应商
	public static String sp = "";
	
	//移动号码段正则表达式：139、138、137、136、135、134、159、150、151、152、158、157、182,183,188、187、182、147号段
	public static String  mobilePhoneReg ="^(((13)[4-9]{1})|((15)[0,1,2,7,8,9]{1})|((18)[2,3,7,8]{1})|(147)|(10648))[0-9]{8}$";

	//appName产品名称
	public static final String APPNAME_CHEXINGZHE = "xfinder4personal";
	public static final String APPNAME_CHEZHANGGUI = "xfinder4company";
	public static final String APPNAME_4SONCARE = "4soncare";
	public static final String APPNAME_ZHENGTONGHUI = "ZhengTongHui";
	public static final String APPNAME_ZHAOTA = "zhaoTa";
	public static final String APPNAME_WOXINGTONG = "woxingtong";
	public static final String APPNAME_CHEQITONG = "cheqitong";

	//组成短信发送序列号的随机号，100以内
	public static int SequenceNoRandomIndex = 0;
	//短信发送序列号:用于判断如果时间发生变化则表示新的一天开始，重新计数
	public static String SequenceNoDate = "";
	//短信发送序列号:每天从零开始递增
	public static int count=0;
	
	//运营商类型标志
	public static int carrier_type_chinamobile=0;//移动
	public static int carrier_type_unionAndTelecom=1;//联通和电信
	
	//上行短信回调接口
	public static String callbackUrl="";
	
	public static void main(String[] args)
	{
		List<String> mobs = new ArrayList<String>();
		mobs.add("13012345678");
		mobs.add("13112345679");
		mobs.add("13212345680");
		mobs.add("13312345681");
		mobs.add("13412345682");
		mobs.add("13512345683");
		mobs.add("13612345684");
		mobs.add("13712345685");
		mobs.add("13812345686");
		mobs.add("13912345687");
		mobs.add("14012345688");
		mobs.add("14112345689");
		mobs.add("14212345690");
		mobs.add("14312345691");
		mobs.add("14412345692");
		mobs.add("14512345693");
		mobs.add("14612345694");
		mobs.add("14712345695");
		mobs.add("14812345696");
		mobs.add("14912345697");
		mobs.add("15012345698");
		mobs.add("15112345699");
		mobs.add("15212345700");
		mobs.add("15312345701");
		mobs.add("15412345702");
		mobs.add("15512345703");
		mobs.add("15612345704");
		mobs.add("15712345705");
		mobs.add("15812345706");
		mobs.add("15912345707");
		mobs.add("16012345708");
		mobs.add("16112345709");
		mobs.add("16212345710");
		mobs.add("16312345711");
		mobs.add("16412345712");
		mobs.add("16512345713");
		mobs.add("16612345714");
		mobs.add("16712345715");
		mobs.add("16812345716");
		mobs.add("16912345717");
		mobs.add("17012345718");
		mobs.add("17112345719");
		mobs.add("17212345720");
		mobs.add("17312345721");
		mobs.add("17412345722");
		mobs.add("17512345723");
		mobs.add("17612345724");
		mobs.add("17712345725");
		mobs.add("17812345726");
		mobs.add("17912345727");
		mobs.add("18012345728");
		mobs.add("18112345729");
		mobs.add("18212345730");
		mobs.add("18312345731");
		mobs.add("18412345732");
		mobs.add("18512345733");
		mobs.add("18612345734");
		mobs.add("18712345735");
		mobs.add("18812345736");
		mobs.add("18912345737");
		
		for (String mob : mobs)
		{
			if(mob.matches(mobilePhoneReg))
			{
				System.out.println(mob+" is cmcc num ");
			}
		}

	}
}
