package com.cmcciot.mat.filemanage.dao;

import com.cmcciot.mat.filemanage.bean.FileUploadBean;

public interface FileUploadDao {
	
	// 存放文件上传成功信息
	public void addFileUploadInfo(FileUploadBean fileUploadBean);
}
