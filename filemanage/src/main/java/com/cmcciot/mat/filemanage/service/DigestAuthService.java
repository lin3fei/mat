package com.cmcciot.mat.filemanage.service;

public interface DigestAuthService {

	boolean digestAuth(String sourceIp, String key);
}