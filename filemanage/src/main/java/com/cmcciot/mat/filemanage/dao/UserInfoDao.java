package com.cmcciot.mat.filemanage.dao;

import java.util.List;

import com.cmcciot.mat.filemanage.bean.UserBean;

public interface UserInfoDao {

	// 将生成的用户名，密码插入到DB
	public String addUserInfo(UserBean userBean);
	
	// 通过用户名、密码查询数据是否存在
	public List<UserBean> findUserInfo(UserBean userBean);
}
