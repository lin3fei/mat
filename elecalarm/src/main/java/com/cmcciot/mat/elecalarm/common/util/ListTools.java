package com.cmcciot.mat.elecalarm.common.util;

import java.util.ArrayList;
import java.util.List;

public class ListTools
{
    
    /**
     * 判断集合是否为空
     * 
     * @param list
     * @return
     */
    public static boolean isEmpty(@SuppressWarnings("rawtypes") List list)
    {
        if (null == list)
        {
            return true;
        }
        else
        {
            if (null != list && list.size() == 0)
            {
                return true;
            }
            return false;
        }
    }
    
    public static <T> List<List<T>> getSubList(List<T> listObj, int groupNum)
    {
        List<List<T>> returnList = new ArrayList<List<T>>();
        // 获取需要拆分的List个数 
        int loopCount = (listObj.size() % groupNum == 0) ? (listObj.size() / groupNum)
                : ((listObj.size() / groupNum) + 1);
        // 开始拆分 
        for (int i = 0; i < loopCount; i++)
        {
            // 子List的起始值 
            int startNum = i * groupNum;
            // 子List的终止值 
            int endNum = (i + 1) * groupNum;
            // 不能整除的时候最后一个List的终止值为原始List的最后一个 
            if (i == loopCount - 1)
            {
                endNum = listObj.size();
            }
            // 拆分List 
            List<T> listObjSub = listObj.subList(startNum, endNum);
            // 保存差分后的List 
            returnList.add(listObjSub);
        }
        return returnList;
    }
}
