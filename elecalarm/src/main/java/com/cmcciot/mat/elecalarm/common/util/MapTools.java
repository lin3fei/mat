/*
 * 文 件 名:  MapTools.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年4月10日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.common.util;

import java.util.Map;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Administrator
 * @version  [版本号, 2014年4月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class MapTools
{
    /**
     * <判断集合是否为空>
     * <功能详细描述>
     * @param map
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Map map)
    {
        if (null == map)
        {
            return true;
        }
        else
        {
            if (null != map && map.size() == 0)
            {
                return true;
            }
            return false;
        }
    }
}
