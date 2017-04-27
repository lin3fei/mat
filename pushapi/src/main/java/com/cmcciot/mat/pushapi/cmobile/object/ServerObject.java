package com.cmcciot.mat.pushapi.cmobile.object;

/**
 * 服务器端配置类
 * 
 * @author PTERO
 * 
 */
public final class ServerObject {

	/**
	 * 接手用户手机地址前缀,有tel:,或者tel:+,或者tel:+86
	 */
	private String destAddreassSign;

	/**
	 * oa手机地址前缀,有tel:,或者tel:+,或者tel:+86
	 */
	private String oaSign;

	/**
	 * fa手机地址前缀,有tel:,或者tel:+,或者tel:+86
	 */
	private String faSign;

	/**
	 * 是否需要信息报告标志位,true为需要状态报告,false为不需要
	 */
	private boolean reportNeedFlag;

	/**
	 * 城市区号
	 */
	private String areaCode;

	/**
	 * SP接入号
	 */
	private String accessNo;

	/**
	 * 协议类型
	 */
	private String protocol;

	/**
	 * 请求URL
	 */
	private String requestUrl;

	/**
	 * 服务密码
	 */
	private String spPassword;

	/**
	 * 服务ID
	 */
	private String spId;

	/**
	 * 时间戳
	 */
	private String timeStamp;

	/**
	 * 生产ID
	 */
	private String productId;

	/**
	 * soap头命名空间
	 */
	private String nameSpace;

	/**
	 * 登录账户
	 */
	private String account;

	/**
	 * 登录密码
	 */
	private String password;

	/**
	 * 企业代码
	 */
	private String enterPriseNo;

	/**
	 * CMPP服务器IP
	 */
	private String cmppServerIp;

	/**
	 * CMPP服务端口
	 */
	private int cmppServerPort;

	/**
	 * 握手消息时间间隔
	 */
	private int activeTestTime;

	// SERVICE_ID 业务代码，默认是中移物联的
	public String serviceId = "wlwobd";

	/**
	 * 接受线程等待时间
	 */
	private int receiveThreadWaitTime;

	/**
	 * @return the spNo
	 */
	public String getAccessNo() {
		return accessNo;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @return the activeTestTime
	 */
	public int getActiveTestTime() {
		return activeTestTime;
	}

	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}

	/**
	 * @return the cmppServerIp
	 */
	public String getCmppServerIp() {
		return cmppServerIp;
	}

	/**
	 * @return the cmppServerPort
	 */
	public int getCmppServerPort() {
		return cmppServerPort;
	}

	/**
	 * @return the destAddreassSign
	 */
	public String getDestAddreassSign() {
		return destAddreassSign;
	}

	/**
	 * @return the enterPriseNo
	 */
	public String getEnterPriseNo() {
		return enterPriseNo;
	}

	/**
	 * @return the faSign
	 */
	public String getFaSign() {
		return faSign;
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @return the oaSign
	 */
	public String getOaSign() {
		return oaSign;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @return the receiveThreadWaitTime
	 */
	public int getReceiveThreadWaitTime() {
		return receiveThreadWaitTime;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * @return the spId
	 */
	public String getSpId() {
		return spId;
	}

	/**
	 * @return the spPassword
	 */
	public String getSpPassword() {
		return spPassword;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return the reportNeedFlag
	 */
	public boolean isReportNeedFlag() {
		return reportNeedFlag;
	}

	/**
	 * @param accessNo
	 *            the accessNo to set
	 */
	public void setAccessNo(String accessNo) {
		if (accessNo == null) {
			this.accessNo = "";
		} else {
			this.accessNo = accessNo;
		}
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @param activeTestTime
	 *            the activeTestTime to set
	 */
	public void setActiveTestTime(int activeTestTime) {
		this.activeTestTime = activeTestTime;
	}

	/**
	 * @param areaCode
	 *            the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		if (areaCode == null) {
			this.areaCode = "";
		} else {
			this.areaCode = areaCode;
		}
	}

	/**
	 * @param cmppServerIp
	 *            the cmppServerIp to set
	 */
	public void setCmppServerIp(String cmppServerIp) {
		this.cmppServerIp = cmppServerIp;
	}

	/**
	 * @param cmppServerPort
	 *            the cmppServerPort to set
	 */
	public void setCmppServerPort(int cmppServerPort) {
		this.cmppServerPort = cmppServerPort;
	}

	/**
	 * @param destAddreassSign
	 *            the destAddreassSign to set
	 */
	public void setDestAddreassSign(String destAddreassSign) {
		if (destAddreassSign == null) {
			this.destAddreassSign = "";
		} else {
			this.destAddreassSign = destAddreassSign;
		}
	}

	/**
	 * @param enterPriseNo
	 *            the enterPriseNo to set
	 */
	public void setEnterPriseNo(String enterPriseNo) {
		this.enterPriseNo = enterPriseNo;
	}

	/**
	 * @param faSign
	 *            the faSign to set
	 */
	public void setFaSign(String faSign) {
		if (faSign == null) {
			this.faSign = "";
		} else {
			this.faSign = faSign;
		}
	}

	/**
	 * @param nameSpace
	 *            the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		if (nameSpace == null) {
			this.nameSpace = "";
		} else {
			this.nameSpace = nameSpace;
		}
	}

	/**
	 * @param oaSign
	 *            the oaSign to set
	 */
	public void setOaSign(String oaSign) {
		if (oaSign == null) {
			this.oaSign = "";
		} else {
			this.oaSign = oaSign;
		}
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		if (productId == null) {
			this.productId = "";
		} else {
			this.productId = productId;
		}
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		if (protocol == null) {
			this.protocol = "";
		} else {
			this.protocol = protocol;
		}
	}

	/**
	 * @param receiveThreadWaitTime
	 *            the receiveThreadWaitTime to set
	 */
	public void setReceiveThreadWaitTime(int receiveThreadWaitTime) {
		this.receiveThreadWaitTime = receiveThreadWaitTime;
	}

	/**
	 * @param reportNeedFlag
	 *            the reportNeedFlag to set
	 */
	public void setReportNeedFlag(boolean reportNeedFlag) {
		this.reportNeedFlag = reportNeedFlag;
	}

	/**
	 * @param requestUrl
	 *            the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		if (requestUrl == null) {
			this.requestUrl = "";
		} else {
			this.requestUrl = requestUrl;
		}
	}

	/**
	 * @param spId
	 *            the spId to set
	 */
	public void setSpId(String spId) {
		if (spId == null) {
			this.spId = "";
		} else {
			this.spId = spId;
		}
	}

	/**
	 * @param spPassword
	 *            the spPassword to set
	 */
	public void setSpPassword(String spPassword) {
		if (spPassword == null) {
			this.spPassword = "";
		} else {
			this.spPassword = spPassword;
		}
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		if (timeStamp == null) {
			this.timeStamp = "";
		} else {
			this.timeStamp = timeStamp;
		}
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		return "ServerObject [destAddreassSign=" + destAddreassSign
				+ ", oaSign=" + oaSign + ", faSign=" + faSign
				+ ", reportNeedFlag=" + reportNeedFlag + ", areaCode="
				+ areaCode + ", accessNo=" + accessNo + ", protocol="
				+ protocol + ", requestUrl=" + requestUrl + ", spPassword="
				+ spPassword + ", spId=" + spId + ", timeStamp=" + timeStamp
				+ ", productId=" + productId + ", nameSpace=" + nameSpace
				+ ", account=" + account + ", password=" + password
				+ ", enterPriseNo=" + enterPriseNo + ", cmppServerIp="
				+ cmppServerIp + ", cmppServerPort=" + cmppServerPort
				+ ", activeTestTime=" + activeTestTime + ", serviceId="
				+ serviceId + ", receiveThreadWaitTime="
				+ receiveThreadWaitTime + "]";
	}

}
