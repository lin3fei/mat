package com.cmcciot.mat.enclosure.manager.controller.userManager;

import java.util.List;
import java.util.Map;
import com.cmcciot.mat.enclosure.business.userManager.service.EnClosuerService;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Ginda.Tseng on 2015/7/24.
 */
@Controller
@RequestMapping("enClosuerManager")
public class EnClosuerMonitorController {

    private static Logger logger = LoggerFactory.getLogger(EnClosuerMonitorController.class);

    @Autowired
    private EnClosuerService enClosuerService;
    
    /**
     * ������ΧȦ��Ϣ
     * @return
     */
    
    @RequestMapping(value="/enClosuerManager/addEnClosuer")
    public ModelAndView addEnClosuer(HttpServletRequest request, String monitorIds,
    		String enclosuName,String enclosuCenter,String enclosumapCenter,
    		String enclosuRadius,String areaName,String weekTimes,
    		String startTime,String endTime,String state,String pauseState,
    		String gaodeGps,String enclsrfixedshowId,String createTime,String enclosuType) {
//        ModelAndView mv = new ModelAndView();
//        EnClosuerMonitorBean enClosuerMonitorBean = null;
        return null;
        }
    
    @RequestMapping(value="queryEnClosuer/{orgId}")
    public ModelAndView queryEnClosuer(HttpServletRequest request, int orgId) {
        ModelAndView mv = new ModelAndView();
        List<Map<String,Object>>  enClosuerList = enClosuerService.selectByOrgId(6);
        mv.addObject("enClosuerList",enClosuerList);
        logger.info("queryEnClosure success");
        return mv;
    }
}
