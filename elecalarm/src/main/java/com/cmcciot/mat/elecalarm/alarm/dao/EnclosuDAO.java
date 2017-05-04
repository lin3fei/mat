package com.cmcciot.mat.elecalarm.alarm.dao;

import java.util.List;

import com.cmcciot.mat.elecalarm.alarm.bean.AlertMessageBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EncloAlarmBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuBean;
import com.cmcciot.mat.elecalarm.alarm.bean.EnclosuLogBean;
import com.cmcciot.mat.elecalarm.alarm.bean.MultipleDataBean;

/**
 * 
 * 围栏信息DAO
 * <功能详细描述>
 * 
 * @author fh
 * @version  [版本号, 2015年4月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface EnclosuDAO {

	// 查找所有添加了电子围栏设备
	public List<EnclosuBean> findDeviceList();

	// 通过围栏ID查找电子围栏设备
	public EnclosuBean findDevice(int encloId);
	
	// 查找围栏告警表中是否已经告警存在了
	public EncloAlarmBean findAlarm(int encloId);
	
	// 添加围栏告警
	public void addAlarm(EncloAlarmBean encloAlarmBean);
	
	// 更新围栏告警表中告警次数
	public void updateAlarm(int encloId, int times);
	
	// 更新围栏表暂停当日告警
	public void updateEncloInfo(int encloId, int pauseState);
	
	// 更新所有围栏表暂停为有效（每日凌晨更新）
	public void updateAllEncloInfo(int pauseState);
	
	// 删除围栏告警
	public void deleteAlarm(int encloId);
	
	// 添加围栏告警日志
	public void addAlarmLog(EnclosuLogBean enclosuLogBean);

	// 添加告警日志
	public void addAlertMessage(AlertMessageBean alertMessageBean);

	// 添加综合数据表
	public void addMultipleData(MultipleDataBean multipleDataBean);
}
