package com.cmcciot.mat.elecalarm.locationmanager.util;

import com.cmcciot.mat.elecalarm.locationmanager.bean.LatLng;
import com.cmcciot.mat.elecalarm.locationmanager.bean.PointM;

public class MapUtils {
	//zoom Ϊ��ͼ���ż���
	public static LatLng fromPixelToLatLng(PointM point, int zoom)
    {
            double lng = point.getx() * 360 / (256 << zoom) - 180;
            double y = 2 * Math.PI * (1 - point.gety() / (128 << zoom));
            
            double z = Math.pow(Math.E, y);
            double siny = (z - 1) / (z + 1);
            double lat = Math.asin(siny) * 180 / Math.PI;
            return new LatLng(lat, lng);
     }

	 //��γ��ת����
     public static PointM formLatLngToPixel(LatLng latLng, int zoom)
     {
            double pixelX = (latLng.getLng() + 180) * (256 << zoom) / 360;
            double siny = Math.sin(latLng.getLat() * Math.PI / 180);
            double y = Math.log((1 + siny) / (1 - siny));
            double pixelY = (128 << zoom) * (1 - y / (2 * Math.PI));

            return new PointM(pixelX, pixelY);
     }
}
