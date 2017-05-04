package com.cmcciot.mat.enclosure.rmi.service;

import java.util.List;
import java.util.Map;

/**
 * 电子围栏服务
 * Created by Ginda.Tseng on 2015/8/24.
 */
public interface HessianGetEnclosureDataService {
	List<Map<String,Object>> getEnclosureDataForIndustry(String orgId);
	
	List<Map<String,Object>> getEnclosureDataByNameForIndustry(String orgId,String eName);
	
	List<Map<String,Object>> getOrgDataForIndustry();
	
	List<Map<String, Object>> getMonitors(Integer orgId);
	
	List <Map<String,Object>> getUnoccupiedMonitor(Map<String, Object> params);
	
	Map<String,Object> getEnclosureDetailForIndustry(String Id);
	
	Map<String,Object> getEnclosureMoreForIndustryUpdate(String Id);
	
	int addEnclosureData(String enclosuName,String startTime,String endTime,String weekTime,
			String enclosuType,String areaName,String members,String enclosuCenter,int enclosuRadius,String state,String pauseState);

	int updateEnclosureData(String enclosuName,String startTime,String endTime,String weekTime,String enclosuType,String areaName,String members,String enclosuCenter,int enclosuRadius,String eId);

	int deleteEnclosureData(String id);
	
	int openEnclosureData(String id,String state);
}
