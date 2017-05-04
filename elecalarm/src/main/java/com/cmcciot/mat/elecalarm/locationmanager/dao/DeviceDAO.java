/*
 * 文 件 名:  DeviceDAO.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月3日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.locationmanager.dao;

import com.cmcciot.mat.elecalarm.locationmanager.bean.DeviceBean;

/**
 * <设备信息查询DAO> <功能详细描述>
 * 
 * @author fh
 * @version [版本号, 2014年4月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface DeviceDAO {

	/**
	 * <查找设备> <功能详细描述>
	 * 
	 * @param deviceId
	 * @return [参数说明]
	 * 
	 * @return DeviceBean [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	DeviceBean findDeviceById(String deviceId);
}
