/*
 * 文 件 名:  DigestAuthUtils.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月9日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.filemanage.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author Administrator
 * @version [版本号, 2014年4月9日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DigestAuthUtils
{

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * 分隔字符串，如果字符串中有双引号("),则忽略。
	 * 
	 * @param str 被分割字符串
	 * @param separatorChar 分隔字符
	 * @return [参数说明]
	 * 
	 * @return String[] [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String[] splitIgnoringQuotes(String str, char separatorChar)
	{
		if (str == null)
		{
			return null;
		}

		int len = str.length();

		if (len == 0)
		{
			return EMPTY_STRING_ARRAY;
		}

		List<String> list = new ArrayList<String>();
		int i = 0;
		int start = 0;
		boolean match = false;

		while (i < len)
		{
			if (str.charAt(i) == '"')
			{
				i++;
				while (i < len)
				{
					if (str.charAt(i) == '"')
					{
						i++;
						break;
					}
					i++;
				}
				match = true;
				continue;
			}
			if (str.charAt(i) == separatorChar)
			{
				if (match)
				{
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (match)
		{
			list.add(str.substring(start, i));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 将数组转换为map
	 * 数组中的每个元素都是 一个 key-value形式的字符串
	 * 
	 * @param array 数组
	 * @param delimiter 分隔数组中元素为 key-value的分隔符
	 * @param removeCharacters 需要去掉的字符
	 * @return [参数说明]
	 * 
	 * @return Map<String,String> [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static Map<String, String> splitEachArrayElementAndCreateMap(
			String[] array, String delimiter, String removeCharacters)
	{
		if ((array == null) || (array.length == 0))
		{
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();

		for (String s : array)
		{
			String postRemove;

			if (removeCharacters == null)
			{
				postRemove = s;
			} else
			{
				postRemove = StringUtils.replace(s, removeCharacters, "");
			}

			String[] splitThisArrayElement = split(postRemove, delimiter);

			if (splitThisArrayElement == null)
			{
				continue;
			}

			map.put(splitThisArrayElement[0].trim(),
					splitThisArrayElement[1].trim());
		}

		return map;
	}

	/**
	 * 将字符串 toSplit，根据delimiter分隔成前后2个字符串
	 * <功能详细描述>
	 * @param toSplit
	 * @param delimiter
	 * @return [参数说明]
	 * 
	 * @return String[] [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String[] split(String toSplit, String delimiter)
	{
		if (toSplit == null || delimiter == null)
		{
			throw new IllegalArgumentException(
					"Delimiter or toSplit can only be one character in length");
		}
		if (delimiter.length() != 1)
		{
			throw new IllegalArgumentException(
					"Delimiter can only be one character in length");
		}

		int offset = toSplit.indexOf(delimiter);

		if (offset < 0)
		{
			return null;
		}

		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + 1);

		return new String[]
		{ beforeDelimiter, afterDelimiter };
	}
	
	public static void main(String[] args)throws Exception
	{
		String s = "sadfsdfs";
		MessageDigest digest = null;
		try
		{
			digest = MessageDigest.getInstance("MD5");
		} 
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("No MD5 algorithm available!");
		}
		System.out.println(new String(Hex.encode(digest.digest(s.getBytes()))));
		System.out.println(new String(digest.digest(s.getBytes("UTF-8"))));
	}
}
