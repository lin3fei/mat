package com.cmcciot.mat.filemanage.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcciot.mat.filemanage.bean.UserBean;
import com.cmcciot.mat.filemanage.dao.UserInfoDao;
import com.cmcciot.mat.filemanage.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {
	
	@Resource
	private UserInfoDao userInfoDao;

	@Override
	public String addUserInfo(UserBean userBean) {
		return userInfoDao.addUserInfo(userBean);
	}

	@Override
	public List<UserBean> findUserInfo(UserBean userBean) {
		// TODO Auto-generated method stub
		return userInfoDao.findUserInfo(userBean);
	}

}
