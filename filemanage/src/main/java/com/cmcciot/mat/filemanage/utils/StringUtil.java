/*
 * 文 件 名:  StringUtils.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月10日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.filemanage.utils;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Administrator
 * @version  [版本号, 2014年4月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StringUtil
{
	/**
	 * 判断字符串是否为空串
	 * <功能详细描述>
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return boolean [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isEmpty(String str)
	{
		if(str==null || str.trim().length()==0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 去掉字符串前后的 引号
	 * <功能详细描述>
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String trimQuot(String str)
	{
		String[] quots = {"\"","'"};
		if(str==null)
		{
			return str;
		}
		str = str.trim();
		for(String quot : quots)
		{
			if(str.startsWith(quot))
			{
				str = str.substring(quot.length());
			}
			if(str.endsWith(quot))
			{
				str = str.substring(0, str.length()-quot.length());
			}
		}
		
		return str;
	}

	public static void main(String[] args)
	{
		String s = "\"wxwtest'ww\"";
		System.out.println(trimQuot(s));
	}
}
