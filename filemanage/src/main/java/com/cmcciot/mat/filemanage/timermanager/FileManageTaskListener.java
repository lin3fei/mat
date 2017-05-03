package com.cmcciot.mat.filemanage.timermanager;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.cmcciot.mat.filemanage.utils.PropertyUtil;

public class FileManageTaskListener implements ServletContextListener
{
    private Timer timer = null;
    
    private static final int TIMER_RUN_TIME_HOUR = Integer.parseInt(PropertyUtil.getValue("timer.runtime.hour"));
    
    private static final int TIMER_RUN_TIME_MINUTE = Integer.parseInt(PropertyUtil.getValue("timer.runtime.minute"));
    
    private static final int TIMER_RUN_TIME_SECOND = Integer.parseInt(PropertyUtil.getValue("timer.runtime.second"));
    
    private static final long PERIOD_DAY = Integer.parseInt(PropertyUtil.getValue("timer.runcycle")) * 60 * 60 * 1000;
    
    public void contextInitialized(ServletContextEvent event)
    {
        Calendar calendar = Calendar.getInstance();
        
        /*** 定制每日2:00执行方法 ***/
        calendar.set(Calendar.HOUR_OF_DAY, TIMER_RUN_TIME_HOUR);
        
        calendar.set(Calendar.MINUTE, TIMER_RUN_TIME_MINUTE);
        
        calendar.set(Calendar.SECOND, TIMER_RUN_TIME_SECOND);
        
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        
        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date()))
        {
            date = this.addDay(date, 1);
        }
        
        timer = new Timer();
        
        FileManageTimerTask task1 = new FileManageTimerTask();
//        FileDeleteTimerTask task2 = new FileDeleteTimerTask();
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(task1, date, PERIOD_DAY);
//        timer.schedule(task2, date, PERIOD_DAY);
    }
    
    public void contextDestroyed(ServletContextEvent event)
    {
        timer.cancel();
        event.getServletContext().log("定时器销毁");
    }
    
    // 增加或减少天数
    public Date addDay(Date date, int num)
    {
        Calendar startDT = Calendar.getInstance();
        
        startDT.setTime(date);
        
        startDT.add(Calendar.DAY_OF_MONTH, num);
        
        return startDT.getTime();
    }
}
