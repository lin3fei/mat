package com.cmcciot.mat.filemanage.service.impl;

import java.util.Arrays;
import java.util.List;
import com.cmcciot.mat.filemanage.service.DigestAuthService;
import com.cmcciot.mat.filemanage.utils.KeyUtil;
import com.cmcciot.mat.filemanage.utils.PropertyUtil;

public class DigestAuthServiceImpl implements DigestAuthService {

	/**
	 * 校验自服务平台之间的请求合法性
	 * 
	 * @param req
	 * @param resp
	 * @param filterChain
	 */
	public boolean digestAuth(String sourceIp, String key) {
		// 取出配置的IP
		String[] ipArray = PropertyUtil.getValue4Array("http.ip.white");
		List<String> listArray = Arrays.asList(ipArray);
		// 判断IP是否是服务管理内网IP
		boolean bIp = listArray.contains(sourceIp);

		if (!bIp) {// 未在配置中找到IP白名单
			return false;
		}

		String localKey = KeyUtil.makeMD5(PropertyUtil.getValue("http.service.id") + PropertyUtil.getValue("http.service.password"));

		if (!key.equals(localKey)) {
			// 密钥不正确
			return false;
		}

		// 验证成功，继续执行。
		return true;
	}

}
