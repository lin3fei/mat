package com.cmcciot.mat.elecalarm.locationmanager.dao;

import com.cmcciot.mat.elecalarm.locationmanager.bean.PointM;

public interface LocationDAO {
	
	
	/**
	 * 根据经纬度查询对应偏移
	 * */
	public PointM queryOffset(PointM point);

}
