package com.cmcciot.mat.elecalarm.sdhttp.service.impl;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cmcciot.mat.elecalarm.common.DatastreamConstant;
import com.cmcciot.mat.elecalarm.common.exception.ErrorNumber;
import com.cmcciot.mat.elecalarm.common.exception.LogicException;
import com.cmcciot.mat.elecalarm.common.util.PropertyUtil;
import com.cmcciot.mat.elecalarm.common.util.StringTools;
import com.cmcciot.mat.elecalarm.sdhttp.bean.OneNetBackInfo;
import com.cmcciot.mat.elecalarm.sdhttp.service.OneNetDataPointService;
import com.cmcciot.mat.elecalarm.sdhttp.tools.OneNetHttpInterfaceTools;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author fh
 * @version  [版本号, 2015年4月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */

@Service("oneNetDataPointService")
public class OneNetDatapointServiceImpl implements OneNetDataPointService
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 查询终端设备的数据点
     * @param deviceID 设备ID
     * 
     * @return String [数据流]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
	public String queryDataPoint(String deviceID) throws LogicException {
    	try {
    		String jsonStr = "";
    		
    	    
    	    String httpSite = PropertyUtil.getValue("httpSite");
    	    
    	    String apiKey = PropertyUtil.getValue("apiKey");
    		
	    	// 判断设备ID是否为空
	    	if (StringTools.isEmptyOrNull(deviceID))
	        {
	            logger.error("参数设备id不能为空");
	            throw new LogicException(ErrorNumber.NULL_ERROR, "参数设备id不能为空");
	        }
	    	
	    	// 通过传参拼接参数
	    	StringBuffer urlParamSb = new StringBuffer();
	    	
	    	urlParamSb.append("&datastream_id=" + DatastreamConstant.LAORENDINGWEI);
	            
	        urlParamSb.append("&sort_time=-1&page=1&per_page=1");
	    	
			String uri = httpSite + "devices/" + deviceID + "/datapoints" + "?" + urlParamSb.substring(1).toString();
	    	
			OneNetBackInfo backInfo = OneNetHttpInterfaceTools.httpGetOneNet(uri, apiKey);
	    	if (backInfo.getErrno() != 0
	                || StringTools.isEmptyOrNull(backInfo.getData()))
	        {
	            logger.debug("未查到指定设备的数据流，错误信息是：" + backInfo.getError());
	            return null;
	        }
	        else
	        {
	            JSONObject jsonObj = (JSONObject) backInfo.getData();
	            jsonStr = jsonObj.toString();
	        }
	    	return jsonStr;
    	} catch (Exception e) {
    		logger.error("查询数据点错误，错误信息是：" + e.toString());
            throw new LogicException(ErrorNumber.ERROR_INTERFACE, "查询数据异常");
    	}
    }

}
