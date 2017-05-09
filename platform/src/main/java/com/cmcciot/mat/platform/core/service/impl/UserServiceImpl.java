package com.cmcciot.mat.platform.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cmcciot.mat.platform.core.bean.UserBean;
import com.cmcciot.mat.platform.core.dao.UserDAO;
import com.cmcciot.mat.platform.core.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource
	private UserDAO userDao;
	
	@Override
	public String sayHi(String name) {
		return "Hi, " + name;
	}

	@Override
	public String queryUser() {
		return JSON.toJSONString(userDao.queryUser(new UserBean()));
	}

}
