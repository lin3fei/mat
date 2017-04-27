package com.cmcciot.mat.pushapi.cmobile.object;

/**
 * 杭州发送短信需要的字段类
 * 
 * @author PTERO
 * 
 */
public final class CmppSendSmsObject
{

	/**
	 * 短信发送序列号
	 */
	private String sequenceNo;

	/**
	 * 短信接收者
	 */
	private String toUserPhone;

	/**
	 * 发送内容
	 */
	private String content;

	/**
	 * 运营商类型:0移动，其它联通和电信
	 */
	private Integer carrierType;

	public Integer getCarrierType()
	{
		return carrierType;
	}

	public void setCarrierType(Integer carrierType)
	{
		this.carrierType = carrierType;
	}

	/**
	 * @return the sequenceNo
	 */
	public String getSequenceNo()
	{
		return sequenceNo;
	}

	/**
	 * @param sequenceNo
	 *            the sequenceNo to set
	 */
	public void setSequenceNo(String sequenceNo)
	{
		this.sequenceNo = sequenceNo;
	}

	/**
	 * @return the toUserPhone
	 */
	public String getToUserPhone()
	{
		return toUserPhone;
	}

	/**
	 * @param toUserPhone
	 *            the toUserPhone to set
	 */
	public void setToUserPhone(String toUserPhone)
	{
		this.toUserPhone = toUserPhone;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

}
