/**
 * 
 */
package com.cmcciot.mat.pushapi.cmobile.cmpp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.cmcciot.mat.pushapi.cmobile.constant.SocketConstant;
import com.cmcciot.mat.pushapi.cmobile.object.CmppSendSmsObject;

/**
 * @author wd
 *
 */
public class SendQueue {

	private static final int maxLength = SocketConstant.MAX_SEND_LIST_LENGTH;
	private static BlockingQueue<CmppSendSmsObject> queue=new ArrayBlockingQueue<CmppSendSmsObject>(maxLength,true);
	
	public static BlockingQueue<CmppSendSmsObject> getSmsSendQueue(){
		return queue;
	}
	
	/**
	 * 将发送任务插入到sms发送队列 队列满的话阻塞等待
	 * @param so
	 * @return
	 * @throws InterruptedException 
	 */
	public static void addSmsTask(CmppSendSmsObject so) throws InterruptedException{
		  queue.put(so);
	}
	
	/**
	 * 获取头部的发送任务，获得以后将删除队列头部的任务
	 * 本方法会阻塞调用者的线程
	 * @return
	 * @throws InterruptedException 
	 */
	public static CmppSendSmsObject takeSmsTask() throws InterruptedException{
		return queue.take();
	}
}
