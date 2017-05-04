package com.cmcciot.mat.enclosure.model.dao;

import java.util.List;
import java.util.Map;

import com.cmcciot.mat.enclosure.model.domain.AlertMessageBean;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerAssocialBean;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerMonitorBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosuLogBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosureDeviceHistorydatastreamBean;
import com.cmcciot.mat.enclosure.model.domain.MultipleDataBean;
import com.cmcciot.mat.enclosure.model.domain.SmsLogsBean;



public interface EnClosuerMonitorMapper {
	
    List<Map<String,Object>> queryInvaildEnClosure();

	int deleteByPrimaryKey(Integer id);
    
    int deleteAssocialByPrimaryKey(Integer id);
    
    int insert(EnClosuerMonitorBean enClosuerMonitorBean);
    
    int insertAssocial(EnClosuerAssocialBean enClosuerAssocialBean);
    
    int insertSelective(EnClosuerMonitorBean enClosuerMonitorBean);
    
    int insertAssocialSelective(EnClosuerAssocialBean enClosuerAssocialBean);

    EnClosuerMonitorBean selectByPrimaryKey(Integer id);
    
    List <Map<String,Object>> selectByOrgId(Integer orgId);
    
    List <Map<String,Object>> selectByOrgIdAndName(EnClosuerMonitorBean enClosuerMonitorBean);
    
    EnClosuerMonitorBean selectById(Integer id);
    
    int updateByPrimaryKeySelective(EnClosuerMonitorBean enClosuerMonitorBean);

    int updateByPrimaryKey(EnClosuerMonitorBean enClosuerMonitorBean);
    
    List <Map<String,Object>> selectIorg();
    
    List <Map<String,Object>> selectMonitor(List<String> paramList);
    
    List <Map<String,Object>> selectThirdIorg(int orgId);

    List <Map<String,Object>> selectUnoccupiedMonitor(Map<String, Object> params);
    
	void addEnClosureDeviceHistoryDataStrams(Map<String, Object> params);

	void addAlarmLog(EnclosuLogBean enclosuLogBean);

	void addAlertMessage(AlertMessageBean amb);

	void addMultipleData(MultipleDataBean multipleDataBean);

	void updateEnClosureDeviceHistoryDataStrams(Map<String, Object> params);

	void addSmsLogs(SmsLogsBean bean);

    
    List <Map<String,Object>> selectIorgAndParIorg(Integer orgId);
    
    List <Map<String,Object>> selectIorgAndParIorgUsed(Integer orgId);
    
    int updateEnclosurestate(String id);

    List<Map<String, Object>> selectBeviceid(String deviceId);
    
    List<Map<String,Object>> selectDeviceInfo(String monitorId);

    int deleteAlertMessage(Map<String, Object> map);

    List<Map<String, Object>> queryAssocialByPrimaryKey(Integer eid);

    int deleteHistorydatastream(Integer id);

    void addEnClosureDeviceHistory(EnclosureDeviceHistorydatastreamBean enBean);
}