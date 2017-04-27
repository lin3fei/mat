package com.cmcciot.mat.pushapi.sms.impl;

import java.util.Date;

/**
 * SmsMsg entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class SmsMsg implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String smsid;
	private Integer receiverNum;
	private String receiverNo;
	private Integer msgType;
	private String msgContent;
	private Integer msgSrcType;
	private String msgSrcKey;
	private String userId;
	private Date creatTime;
	private Integer priority;
	private Integer processStatus;
	private Date processTime;
	private String processIsmgno;
	private String sysSmsId;

	// Constructors

	/** default constructor */
	public SmsMsg() {
	}

	/** minimal constructor */
	public SmsMsg(String smsid) {
		this.smsid = smsid;
	}

	/** full constructor */
	public SmsMsg(String smsid, Integer receiverNum, String receiverNo,
			Integer msgType, String msgContent, Integer msgSrcType,
			String msgSrcKey, String userId, Date creatTime, Integer priority,
			Integer processStatus, Date processTime, String processIsmgno,
			String sysSmsId) {
		this.smsid = smsid;
		this.receiverNum = receiverNum;
		this.receiverNo = receiverNo;
		this.msgType = msgType;
		this.msgContent = msgContent;
		this.msgSrcType = msgSrcType;
		this.msgSrcKey = msgSrcKey;
		this.userId = userId;
		this.creatTime = creatTime;
		this.priority = priority;
		this.processStatus = processStatus;
		this.processTime = processTime;
		this.processIsmgno = processIsmgno;
		this.sysSmsId = sysSmsId;
	}

	// Property accessors

	public String getSmsid() {
		return this.smsid;
	}

	public void setSmsid(String smsid) {
		this.smsid = smsid;
	}

	public Integer getReceiverNum() {
		return this.receiverNum;
	}

	public void setReceiverNum(Integer receiverNum) {
		this.receiverNum = receiverNum;
	}

	public String getReceiverNo() {
		return this.receiverNo;
	}

	public void setReceiverNo(String receiverNo) {
		this.receiverNo = receiverNo;
	}

	public Integer getMsgType() {
		return this.msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public String getMsgContent() {
		return this.msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Integer getMsgSrcType() {
		return this.msgSrcType;
	}

	public void setMsgSrcType(Integer msgSrcType) {
		this.msgSrcType = msgSrcType;
	}

	public String getMsgSrcKey() {
		return this.msgSrcKey;
	}

	public void setMsgSrcKey(String msgSrcKey) {
		this.msgSrcKey = msgSrcKey;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatTime() {
		return this.creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getProcessStatus() {
		return this.processStatus;
	}

	public void setProcessStatus(Integer processStatus) {
		this.processStatus = processStatus;
	}

	public Date getProcessTime() {
		return this.processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	public String getProcessIsmgno() {
		return this.processIsmgno;
	}

	public void setProcessIsmgno(String processIsmgno) {
		this.processIsmgno = processIsmgno;
	}

	public String getSysSmsId() {
		return this.sysSmsId;
	}

	public void setSysSmsId(String sysSmsId) {
		this.sysSmsId = sysSmsId;
	}

}