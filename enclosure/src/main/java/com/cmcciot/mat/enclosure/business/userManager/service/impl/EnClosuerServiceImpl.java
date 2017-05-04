package com.cmcciot.mat.enclosure.business.userManager.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.enclosure.business.userManager.service.EnClosuerService;
import com.cmcciot.mat.enclosure.model.dao.EnClosuerMonitorMapper;
import com.cmcciot.mat.enclosure.model.domain.AlertMessageBean;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerAssocialBean;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerMonitorBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosuLogBean;
import com.cmcciot.mat.enclosure.model.domain.EnclosureDeviceHistorydatastreamBean;
import com.cmcciot.mat.enclosure.model.domain.MultipleDataBean;
import com.cmcciot.mat.enclosure.model.domain.SmsLogsBean;

/**
 * Created by hu.qiao on 2015/8/19.
 */
@Service("enClosuerService")
public class EnClosuerServiceImpl implements EnClosuerService {


    private static Logger logger = LoggerFactory.getLogger(EnClosuerServiceImpl.class);

    @Autowired
    private EnClosuerMonitorMapper enClosuerMonitorMapper;
  
    @Override
	public int addEnClosuer(EnClosuerMonitorBean enClosuerMonitorBean) {
		return enClosuerMonitorMapper.insertSelective(enClosuerMonitorBean);
	}
    
    @Override
	public int updateEnClosuer(EnClosuerMonitorBean enClosuerMonitorBean) {
		return enClosuerMonitorMapper.updateByPrimaryKeySelective(enClosuerMonitorBean);
	}
    
    @Override
	public List<Map<String, Object>> queryEnClosuer(String associalId) {
		return null;
	}
    
    @Override
	public List<Map<String,Object>> selectByOrgId(Integer orgId) {
		return enClosuerMonitorMapper.selectByOrgId(orgId);
	}

	@Override
	public EnClosuerMonitorBean selectById(Integer Id) {
		return enClosuerMonitorMapper.selectById(Id);
	}

	@Override
	public int addEnClosuerAssocial(EnClosuerAssocialBean enClosuerAssocialBean) {
		return enClosuerMonitorMapper.insertAssocialSelective(enClosuerAssocialBean);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return enClosuerMonitorMapper.deleteByPrimaryKey(id);
	}
	
	@Override
	public int deleteAssocialByPrimaryKey(Integer id) {
		return enClosuerMonitorMapper.deleteAssocialByPrimaryKey(id);
	}
	
	@Override
	public List<Map<String, Object>> selectIorg() {
		return enClosuerMonitorMapper.selectIorg();
	}
	
	@Override
	public List<Map<String, Object>> selectMonitor(List<String> paramList) {
		return enClosuerMonitorMapper.selectMonitor(paramList);
	}

	@Override

	public List<Map<String,Object>> queryInvaildEnClosure() {
		
		return enClosuerMonitorMapper.queryInvaildEnClosure();
	}

	@Override
	public void addEnClosureDeviceHistoryDataStrams(Map<String, Object> params) {
	    
		enClosuerMonitorMapper.addEnClosureDeviceHistoryDataStrams(params);
	}

	@Override
	public void addAlarmLog(EnclosuLogBean enclosuLogBean) {
		enClosuerMonitorMapper.addAlarmLog(enclosuLogBean);
	}

	@Override
	public void addAlertMessage(AlertMessageBean amb) {
		enClosuerMonitorMapper.addAlertMessage(amb);
	}

	@Override
	public void addMultipleData(MultipleDataBean multipleDataBean) {
		enClosuerMonitorMapper.addMultipleData(multipleDataBean);
	}

	@Override
	public void updateEnClosureDeviceHistoryDataStrams(Map<String, Object> params) {
		enClosuerMonitorMapper.updateEnClosureDeviceHistoryDataStrams(params);
	}

	@Override
	public void addSmsLogs(SmsLogsBean bean) {
		enClosuerMonitorMapper.addSmsLogs(bean);
	}
		
	@Override
	public List<Map<String, Object>> selectIorgAndParIorg(Integer orgId) {
		return enClosuerMonitorMapper.selectIorgAndParIorg(orgId);
	}

	@Override
	public List<Map<String, Object>> selectByOrgIdAndName(EnClosuerMonitorBean enClosuerMonitorBean) {
		return enClosuerMonitorMapper.selectByOrgIdAndName(enClosuerMonitorBean);
	}

	@Override
	public List<Map<String, Object>> selectIorgAndParIorgUsed(Integer orgId) {
		return enClosuerMonitorMapper.selectIorgAndParIorgUsed(orgId);
	}

	@Override
	public List<Map<String, Object>> selectUnoccupiedMonitor(Map<String, Object> params) {
		return enClosuerMonitorMapper.selectUnoccupiedMonitor(params);
	}

	@Override
	public List<Map<String, Object>> selectThirdIorg(int orgId) {
		return enClosuerMonitorMapper.selectThirdIorg(orgId);
	}

    @Override
    public List<Map<String, Object>> selectBeviceid(String deviceId) {
        return enClosuerMonitorMapper.selectBeviceid(deviceId);
    }

	@Override
	public  List<Map<String,Object>> selectDeviceInfo(String monitorId) {
		return enClosuerMonitorMapper.selectDeviceInfo(monitorId);
	}

    @Override
    public int deleteAlertMessage(Integer id,Integer deviceId)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("deviceId", deviceId);
        logger.info(id.toString());
        return enClosuerMonitorMapper.deleteAlertMessage(map);
    }

    @Override
    public List<Map<String, Object>> queryAssocialByPrimaryKey(Integer eid)
    {
        return enClosuerMonitorMapper.queryAssocialByPrimaryKey(eid);
    }

    @Override
    public int deleteHistorydatastream(Integer eid)
    {
        return enClosuerMonitorMapper.deleteHistorydatastream(eid);
    }

    @Override
    public void addEnClosureDeviceHistory(
            EnclosureDeviceHistorydatastreamBean enBean)
    {
        enClosuerMonitorMapper.addEnClosureDeviceHistory(enBean);
        
    }

}
