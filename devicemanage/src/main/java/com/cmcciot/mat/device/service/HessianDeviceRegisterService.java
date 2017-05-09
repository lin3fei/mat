package com.cmcciot.mat.device.service;

import java.util.Map;

public interface HessianDeviceRegisterService {
	
	String deviceRegist(Map<String,Object> map);

	String getUser(String account);

}
