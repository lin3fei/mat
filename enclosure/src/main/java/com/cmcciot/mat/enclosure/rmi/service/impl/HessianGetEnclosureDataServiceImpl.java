package com.cmcciot.mat.enclosure.rmi.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmcciot.mat.enclosure.business.userManager.service.EnClosuerService;
import com.cmcciot.mat.enclosure.model.dao.EnClosuerMonitorMapper;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerAssocialBean;
import com.cmcciot.mat.enclosure.model.domain.EnClosuerMonitorBean;
import com.cmcciot.mat.enclosure.rmi.service.HessianGetEnclosureDataService;
/**
 * Created by Ginda.Tseng on 2015/8/24.
 */

public class HessianGetEnclosureDataServiceImpl implements HessianGetEnclosureDataService {

    private Log logger = LogFactory.getLog(this.getClass());
    
    
    private EnClosuerService enClosuerService;

	public EnClosuerService getEnClosuerService() {
		return enClosuerService;
	}

	public void setEnClosuerService(EnClosuerService enClosuerService) {
		this.enClosuerService = enClosuerService;
	}
	
	@Override
	public List<Map<String,Object>> getEnclosureDataForIndustry(String orgId) {
		logger.info("  获取到部门ID:  "+orgId);
		int oId = Integer.parseInt(orgId);
		List<Map<String,Object>>  enClosuerList = enClosuerService.selectByOrgId(oId);
		if(enClosuerList == null){
			logger.info("  没有部门ID:  "+orgId+" 的围栏信息!");
		}else{
			logger.info("  EnClosuerList:  "+enClosuerList.size());
			for(int i=0;i<enClosuerList.size();i++){
				logger.info("ENCLOSU_NAME:" + enClosuerList.get(i).get("ENCLOSU_NAME"));
				logger.info("ENCLOSU_CENTER:" + enClosuerList.get(i).get("ENCLOSU_CENTER"));
			}
		}
      return enClosuerList;
	}

	@Override
	public int addEnclosureData(String enclosuName,String startTime,String endTime,String weekTime,
			String enclosuType,String areaName,String members,String enclosuCenter,int enclosuRadius,String state,String pauseState) {
			logger.info("进入新增围栏接口~~~");
			EnClosuerMonitorBean enClosuerMonitorBean =new EnClosuerMonitorBean();
			enClosuerMonitorBean.setEnclosuName(enclosuName);
			enClosuerMonitorBean.setEnclosuCenter(enclosuCenter);
			enClosuerMonitorBean.setEnclosuRadius(enclosuRadius);
			enClosuerMonitorBean.setStartTime(startTime);
			enClosuerMonitorBean.setEndTime(endTime);
			enClosuerMonitorBean.setWeekTime(weekTime);
			enClosuerMonitorBean.setEnclosuType(enclosuType);
			enClosuerMonitorBean.setAreaName(areaName);
			enClosuerMonitorBean.setEnclosumapCenter(areaName);
			enClosuerMonitorBean.setPauseState("0");
			enClosuerMonitorBean.setState("0");
			Date dt= new Date();
			Long time= dt.getTime();
			String nowLongTime = time.toString();
			enClosuerMonitorBean.setCreateTime(nowLongTime);
			int enClosuerResult =enClosuerService.addEnClosuer(enClosuerMonitorBean);
			logger.info("开始新增围栏关联信息表~~~~  返回围栏ID为" + enClosuerMonitorBean.getId());
			EnClosuerAssocialBean enClosuerAssocialBean = new EnClosuerAssocialBean();
			String enClosuerId =enClosuerMonitorBean.getId();
			enClosuerAssocialBean.setEnclosuId(enClosuerId);
			logger.info("member info~~~~ " + members);
			JSONArray meJson = JSONArray.fromObject(members);
			for(int i=0;i<meJson.size();i++){
				JSONObject memb = meJson.getJSONObject(i);
				String monitorId = memb.getString("monitorId");
				String deviceId = "";
				List<Map<String,Object>> deviceInfoList = enClosuerService.selectDeviceInfo(monitorId);
				if(deviceInfoList != null){
					int tmpid = (int) deviceInfoList.get(0).get("ID");
					deviceId = String.valueOf(tmpid);
				}
				String orgId = memb.getString("orgId");
				enClosuerAssocialBean.setOrg_id(orgId);
				enClosuerAssocialBean.setMonitorId(monitorId);
				enClosuerAssocialBean.setDeviceId(deviceId);
				int addEnClosuerResult = enClosuerService.addEnClosuerAssocial(enClosuerAssocialBean);
				logger.info("orgId " + orgId);
				logger.info("给"+ memb.getString("monitorName") +" monitorId  为" 
				+ monitorId +" deviceId 为"+ deviceId +"  添加   id 为" + enClosuerId + "  的围栏!成功 "+addEnClosuerResult +"次！");
			}
			logger.info("encloId  " + enClosuerMonitorBean.getId());
			return enClosuerResult;
	}

	@Override
	public int updateEnclosureData(String enclosuName,String startTime,String endTime,String weekTime,String enclosuType,String areaName,String members,String enclosuCenter,int enclosuRadius,String eId) {
		
		/*正在更新围栏基本信息*/
		EnClosuerMonitorBean enClosuerMonitorBean =new EnClosuerMonitorBean();
		enClosuerMonitorBean.setEnclosuName(enclosuName);
		enClosuerMonitorBean.setStartTime(startTime);
		enClosuerMonitorBean.setEndTime(endTime);
		enClosuerMonitorBean.setWeekTime(weekTime);
		enClosuerMonitorBean.setEnclosuType(enclosuType);
		enClosuerMonitorBean.setAreaName(areaName);
		enClosuerMonitorBean.setEnclosuCenter(enclosuCenter);
		enClosuerMonitorBean.setEnclosuRadius(enclosuRadius);
		enClosuerMonitorBean.setId(eId);
		enClosuerService.updateEnClosuer(enClosuerMonitorBean);
		logger.info("正在更新围栏基本信息~");
		/*删除围栏关联基本信息*/
		int deleteRows1 = enClosuerService.deleteAssocialByPrimaryKey(Integer.parseInt(eId));
		logger.info("删除ID  "+eId+" 的围栏关联基本信息   "+deleteRows1+"  条");
		/*删除围栏关联基本信息*/
		logger.info(members);
		EnClosuerAssocialBean enClosuerAssocialBean = new EnClosuerAssocialBean();
		enClosuerAssocialBean.setEnclosuId(eId);
		logger.info("updateEnclosureData " + members);
		JSONArray meJson = JSONArray.fromObject(members);
		for(int i=0;i<meJson.size();i++){
			JSONObject memb = meJson.getJSONObject(i);
			String monitorId = memb.getString("monitorId");
			String deviceId = "";
			List<Map<String,Object>> deviceInfoList = enClosuerService.selectDeviceInfo(monitorId);
			if(deviceInfoList != null){
				int tmpid = (int) deviceInfoList.get(0).get("ID");
				deviceId = String.valueOf(tmpid);
			}
			String orgId = memb.getString("orgId");
			enClosuerAssocialBean.setOrg_id(orgId);
			enClosuerAssocialBean.setMonitorId(monitorId);
			enClosuerAssocialBean.setDeviceId(deviceId);
			int addEnClosuerResult = enClosuerService.addEnClosuerAssocial(enClosuerAssocialBean);
			logger.info("orgId " + orgId);
			logger.info("给"+ memb.getString("monitorName") +" monitorId  为" 
			+ monitorId +"  添加   id 为" + eId + "  的围栏!成功 "+addEnClosuerResult +"次！");
		}
		return 0;
	}

	@Override
	public int deleteEnclosureData(String id) {
		logger.info("正在删除id = " + id+"  的围栏!");
		int eid= Integer.parseInt(id);
		
		int deviceId = 0 ;
		List<Map<String, Object>> list = enClosuerService.queryAssocialByPrimaryKey(eid);
		for (Map<String, Object> map : list)
        {
	      if (map !=null && !map.isEmpty())
	        {
	          deviceId = (int) map.get("DEVICE_ID");
	        }
        }
		int deleteRows = enClosuerService.deleteByPrimaryKey(eid);
		int deleteRows1 = enClosuerService.deleteAssocialByPrimaryKey(eid);
		int deleteRows2 = enClosuerService.deleteAlertMessage(eid,deviceId);
		int deleteRows3 = enClosuerService.deleteHistorydatastream(eid);
		logger.info("删除了 " + deleteRows+"  行 围栏!");
		logger.info("删除了 " + deleteRows1+"  行 监控对象!");
		logger.info("删除了 " + deleteRows2+"  行 监控对象越界告警记录!");
		logger.info("删除了 " + deleteRows3+"  行 围栏设备历史记录!");
		return deleteRows;
	}

	@Override
	public Map<String, Object> getEnclosureDetailForIndustry(String Id) {
		int eId = Integer.parseInt(Id);
		EnClosuerMonitorBean enClosuerMonitorBean =new EnClosuerMonitorBean();
		enClosuerMonitorBean = enClosuerService.selectById(eId);
		Map<String, Object> eMap = new HashMap<String, Object>();
		eMap.put("enclosuId", enClosuerMonitorBean.getId());
		eMap.put("enclosuName", enClosuerMonitorBean.getEnclosuName());
		eMap.put("areaName", enClosuerMonitorBean.getAreaName());
		eMap.put("enclosuCenter", enClosuerMonitorBean.getEnclosuCenter());
		eMap.put("enclosuRadius", enClosuerMonitorBean.getEnclosuRadius());
		eMap.put("weekTime", enClosuerMonitorBean.getWeekTime());
		eMap.put("startTime", enClosuerMonitorBean.getStartTime());
		eMap.put("endTime", enClosuerMonitorBean.getEndTime());
		eMap.put("enclosuType", enClosuerMonitorBean.getEnclosuType());
		eMap.put("state", enClosuerMonitorBean.getState());
		List<Map<String, Object>> monitorAndIorg = enClosuerService.selectIorgAndParIorg(eId);
		eMap.put("monitorList", monitorAndIorg);
		return eMap;
	}

	@Override
	public List<Map<String, Object>> getOrgDataForIndustry() {
		// TODO 查询所有可用部门
		return enClosuerService.selectIorg();
	}

	@Override
	public List<Map<String, Object>> getMonitors(Integer orgId) {
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~接口调用: 查询指定部门下的所有人员 " + orgId);
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.valueOf(orgId));
		List<Map<String, Object>> thirdIorg = enClosuerService.selectThirdIorg(orgId);
		List<String> paramList = new ArrayList<String>();
		paramList.add(0, String.valueOf(orgId));
		if(thirdIorg.size() > 0){
			for(int i = 0;i<thirdIorg.size(); i++){
				paramList.add(i+1, String.valueOf(thirdIorg.get(i).get("IORG_ID")));
			}
		}
		List<Map<String, Object>> resultList = enClosuerService.selectMonitor(paramList);
		logger.info(" 查询部门"+ paramList +" 下的所有人员结果共: " + resultList.size()+" 条");
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> getUnoccupiedMonitor(
			Map<String, Object> params) {
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~接口调用: 查询指定部门下未被监控的对象!");
		logger.info("params eId:  "+params.get("eId")+"  orgId:  "+ params.get("orgId"));
		int orgId = (int)params.get("orgId");
		List<Map<String, Object>> thirdIorg = enClosuerService.selectThirdIorg(orgId);
		List<String> paramList = new ArrayList<String>();
		paramList.add(0, String.valueOf(orgId));
		if(thirdIorg.size() > 0){
			for(int i = 0;i<thirdIorg.size(); i++){
				paramList.add(i+1, String.valueOf(thirdIorg.get(i).get("IORG_ID")));
			}
		}
		Map<String, Object> paramsNew = new HashMap<String, Object>();
		paramsNew.put("list", paramList);
		paramsNew.put("eId", params.get("eId"));
		logger.info(" 查询部门"+ orgId +" 下未被监控的所有人员结果共: " + paramList.size()+" 条!");
		return enClosuerService.selectUnoccupiedMonitor(paramsNew);
	}
	
	@Override
	public int openEnclosureData(String id,String state) {
		EnClosuerMonitorBean enClosuerMonitorBean = new EnClosuerMonitorBean();
		enClosuerMonitorBean.setId(id);
		enClosuerMonitorBean.setState(state);
		logger.info("接口调用: 更新围栏状态 "+ "正在将围栏Id为  " + id +" 的围栏，状态改为   "+state);
		return enClosuerService.updateEnClosuer(enClosuerMonitorBean);
	}

	@Override
	public List<Map<String, Object>> getEnclosureDataByNameForIndustry(String orgId,
			String eName) {
		EnClosuerMonitorBean enClosuerMonitorBean = new EnClosuerMonitorBean();
		enClosuerMonitorBean.setOrgId(orgId);
		enClosuerMonitorBean.setEnclosuName(eName);
		return enClosuerService.selectByOrgIdAndName(enClosuerMonitorBean);
	}

	@Override
	public Map<String, Object> getEnclosureMoreForIndustryUpdate(String Id) {
		int eId = Integer.parseInt(Id);
		EnClosuerMonitorBean enClosuerMonitorBean =new EnClosuerMonitorBean();
		enClosuerMonitorBean = enClosuerService.selectById(eId);
		Map<String, Object> eMap = new HashMap<String, Object>();
		eMap.put("enclosuId", enClosuerMonitorBean.getId());
		eMap.put("enclosuName", enClosuerMonitorBean.getEnclosuName());
		eMap.put("areaName", enClosuerMonitorBean.getAreaName());
		eMap.put("enclosuCenter", enClosuerMonitorBean.getEnclosuCenter());
		eMap.put("enclosuRadius", enClosuerMonitorBean.getEnclosuRadius());
		eMap.put("weekTime", enClosuerMonitorBean.getWeekTime());
		eMap.put("startTime", enClosuerMonitorBean.getStartTime());
		eMap.put("endTime", enClosuerMonitorBean.getEndTime());
		eMap.put("enclosuType", enClosuerMonitorBean.getEnclosuType());
		eMap.put("state", enClosuerMonitorBean.getState());
		List<Map<String, Object>> monitorAndIorg = enClosuerService.selectIorgAndParIorg(eId);
		eMap.put("monitorList", monitorAndIorg);/*查询到围栏下的所有监控对象*/
		List<Map<String, Object>> monitorAndIorgExisted = enClosuerService.selectIorgAndParIorgUsed(eId);
		eMap.put("occupiedMonitorList", monitorAndIorgExisted);/*查询到围栏下的所有监控对象*/
		return eMap;
	}


}
