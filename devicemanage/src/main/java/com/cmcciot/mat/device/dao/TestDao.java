package com.cmcciot.mat.device.dao;

import java.util.List;

import com.cmcciot.mat.device.bean.User;

public interface TestDao {
	
	List<User> getUser(String name);

}