package com.cmcciot.mat.device.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cmcciot.mat.device.bean.User;
import com.cmcciot.mat.device.dao.TestDao;
import com.cmcciot.mat.device.service.HessianDeviceRegisterService;

@Service("hessianDeviceRegisterService")
public class HessianDeviceRegisterServiceImpl implements HessianDeviceRegisterService {
	
	@Resource
	private TestDao testDao;

	@Override
	public String deviceRegist(Map<String, Object> map) {
		return JSON.toJSONString(map);
	}
	
	@Override
	public String getUser(String account) {
		List<User> r = null;
		try {
			r = testDao.getUser(account);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(r);
	}

}