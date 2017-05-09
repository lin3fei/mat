package com.cmcciot.mat.pay.dao;

import java.util.List;

import com.cmcciot.mat.pay.bean.User;

public interface TestDao {
	
	List<User> getUser(String name);

}