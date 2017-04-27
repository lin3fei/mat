package com.cmcciot.mat.pushapi.sms.intf;

public interface SmsSender extends Runnable
{
	/**
	 * 简单短信异步发送接口
	 * 
	 * @param cellNo
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @return 是否发送成功，这个值不会被最终返回
	 */
	boolean send(String cellNo, String content);

	/**
	 * 带业务关联的短信异步发送接口
	 * 
	 * @param cellNo
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @param busMsgId
	 *            业务消息标识
	 * @param userId
	 *            发送的用户标识
	 * @return 是否发送成功，这个值不会被最终返回
	 */
	boolean send(String cellNo, String content, String busMsgId, String userId);

	/**
	 * 简单短信同步发送接口
	 * 
	 * @param cellNo
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @return 是否发送成功，可以直接到达用户界面
	 */
	boolean sendOnline(String cellNo, String content);

	/**
	 * 带业务关联的短信同步发送接口
	 * 
	 * @param cellNo
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @param busMsgId
	 *            业务消息标识
	 * @param userId
	 *            发送的用户标识
	 * @return 是否发送成功，可以直接到达用户界面
	 */
	boolean sendOnline(String cellNo, String content, String busMsgId, String userId);
}
