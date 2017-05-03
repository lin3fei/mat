package com.cmcciot.mat.filemanage.bean;

import java.io.Serializable;

/**
 * <生成用户名、密码Bean>
 * 
 * @author 傅豪
 * @version [版本号, 2014年11月10日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class FileUploadBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 文件服务器ip
	 */
	private String ip;
	
	/**
	 * 文件名
	 */
	private String fileName;
	
	/**
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 存放文件路径
	 */
	private String filePath;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
