package com.cmcciot.mat.elecalarm.sdhttp.service;

import com.cmcciot.mat.elecalarm.common.exception.LogicException;

/**
 * 操作设备的数据点
 * <功能详细描述>
 * 
 * @author  fh
 * @version  [版本号, 2015年3月30日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface OneNetDataPointService
{
    
    /**
     * 查询最新终端设备的数据点
     * @param deviceID 设备ID
     * 
     * @return String [数据流]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    String queryDataPoint(String deviceID) throws LogicException;
}
