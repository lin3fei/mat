package com.cmcciot.mat.filemanage.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.cmcciot.mat.filemanage.bean.FileUploadBean;
import com.cmcciot.mat.filemanage.dao.FileUploadDao;
import com.cmcciot.mat.filemanage.service.FileService;

@Service
public class FileServiceImpl implements FileService {
	
	@Resource
	private FileUploadDao fileUploadDao;


	@Override
	public void addFileUploadInfo(FileUploadBean fileUploadBean) {
		fileUploadDao.addFileUploadInfo(fileUploadBean);
	}

}