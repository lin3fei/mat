package com.cmcciot.mat.elecalarm.sdhttp.service;

public interface PushSmsService {

	void pushSms(String deviceId, double lon, double lat, String encloId, String type, int gsm, int battery, String time);

	void RegularReportingsettings(String deviceId, int freq, int duration, int resetAs);
}
