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
package com.cmcciot.mat.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author Administrator
 * @version [版本号, 2014年4月10日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class StringUtil {
	private static ObjectMapper objectMapper = null;

	static Logger logger = LoggerFactory.getLogger(StringUtil.class);

	/**
	 * 判断字符串是否为空串 <功能详细描述>
	 * 
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return boolean [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 去掉字符串前后的 引号 <功能详细描述>
	 * 
	 * @param str
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String trimQuot(String str) {
		String[] quots = { "\"", "'" };
		if (str == null) {
			return str;
		}
		str = str.trim();
		for (String quot : quots) {
			if (str.startsWith(quot)) {
				str = str.substring(quot.length());
			}
			if (str.endsWith(quot)) {
				str = str.substring(0, str.length() - quot.length());
			}
		}

		return str;
	}

	/**
	 * 将流转换为字符串 <功能详细描述>
	 * 
	 * @param is
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String convertStreamToString(InputStream is) {
		StringBuilder sb1 = new StringBuilder();
		byte[] bytes = new byte[4096];
		int size = 0;

		try {
			while ((size = is.read(bytes)) > 0) {
				String str = new String(bytes, 0, size, "UTF-8");
				sb1.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb1.toString();
	}

	@SuppressWarnings("finally")
	public static String replaceSensInfo(String str) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = jsonToMap(str);
			String password = (String) map.get("password");
			String phoneNo = (String) map.get("phoneNo");
			String token = (String) map.get("token");
			String newPassword = (String) map.get("newPassword");
			String oldPassword = (String) map.get("oldPassword");
			if (!isEmpty(password)) {
				map.put("password", "******");
			}
			if (!isEmpty(phoneNo)) {
				map.put("phoneNo", "******");
			}
			if (!isEmpty(token)) {
				map.put("token", "******");
			}
			if (!isEmpty(newPassword)) {
				map.put("newPassword", "******");
			}
			if (!isEmpty(oldPassword)) {
				map.put("oldPassword", "******");
			}
			str = mapToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return str;
		}

	}

	/**
	 * json转化为map
	 * 
	 * @param json
	 * @return [参数说明]
	 * 
	 * @return Map<String,Object> [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(json, Map.class);

			for (String s : map.keySet()) {
				Object value = map.get(s);
				if (value != null) {
					String type = map.get(s).getClass().getSimpleName();
					// 将integer类型的转化为String
					if (type.equals("Integer")) {
						map.put(s, ((Integer) map.get(s)).toString());
					}
					// 将long类型的转化为String
					if (type.equals("Long")) {
						map.put(s, ((Long) map.get(s)).toString());
					}
				}
			}
		} catch (Exception e) {
			logger.error("消息格式错误:" + json + "-----" + e);
			map = new HashMap<String, Object>();
			map.put("jsonError", "消息格式错误");
		}
		return map;
	}

	/**
	 * map转化为json <功能详细描述>
	 * 
	 * @param map
	 * @return [参数说明]
	 * 
	 * @return String [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static String mapToJson(Map map) {
		String json = "{\"errorCode\":1,\"description\":\"服务器内部异常\"}";
		objectMapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		JsonGenerator gener;
		try {
			gener = new JsonFactory().createJsonGenerator(sw);
			objectMapper.writeValue(gener, map);
			gener.close();
			json = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * 分隔字符串，如果字符串中有双引号("),则忽略。
	 * 
	 * @param str
	 *            被分割字符串
	 * @param separatorChar
	 *            分隔字符
	 * @return [参数说明]
	 * 
	 * @return String[] [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String[] splitIgnoringQuotes(String str, char separatorChar) {
		if (str == null) {
			return null;
		}

		int len = str.length();

		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}

		List<String> list = new ArrayList<String>();
		int i = 0;
		int start = 0;
		boolean match = false;

		while (i < len) {
			if (str.charAt(i) == '"') {
				i++;
				while (i < len) {
					if (str.charAt(i) == '"') {
						i++;
						break;
					}
					i++;
				}
				match = true;
				continue;
			}
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (match) {
			list.add(str.substring(start, i));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 将数组转换为map 数组中的每个元素都是 一个 key-value形式的字符串
	 * 
	 * @param array
	 *            数组
	 * @param delimiter
	 *            分隔数组中元素为 key-value的分隔符
	 * @param removeCharacters
	 *            需要去掉的字符
	 * @return [参数说明]
	 * 
	 * @return Map<String,String> [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static Map<String, String> splitEachArrayElementAndCreateMap(String[] array, String delimiter,
			String removeCharacters) {
		if ((array == null) || (array.length == 0)) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();

		for (String s : array) {
			String postRemove;

			if (removeCharacters == null) {
				postRemove = s;
			} else {
				postRemove = StringUtils.replace(s, removeCharacters, "");
			}

			String[] splitThisArrayElement = split(postRemove, delimiter);

			if (splitThisArrayElement == null) {
				continue;
			}

			map.put(splitThisArrayElement[0].trim(), splitThisArrayElement[1].trim());
		}

		return map;
	}

	/**
	 * 将字符串 toSplit，根据delimiter分隔成前后2个字符串 <功能详细描述>
	 * 
	 * @param toSplit
	 * @param delimiter
	 * @return [参数说明]
	 * 
	 * @return String[] [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static String[] split(String toSplit, String delimiter) {
		if (toSplit == null || delimiter == null) {
			throw new IllegalArgumentException("Delimiter or toSplit can only be one character in length");
		}
		if (delimiter.length() != 1) {
			throw new IllegalArgumentException("Delimiter can only be one character in length");
		}

		int offset = toSplit.indexOf(delimiter);

		if (offset < 0) {
			return null;
		}

		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + 1);

		return new String[] { beforeDelimiter, afterDelimiter };
	}

	public static void main(String[] args) {
		String s = "\"wxwtest'ww\"";
		System.out.println(trimQuot(s));
	}
}
