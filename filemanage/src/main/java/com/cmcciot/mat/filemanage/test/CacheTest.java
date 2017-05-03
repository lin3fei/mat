package com.cmcciot.mat.filemanage.test;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

import com.cmcciot.mat.filemanage.cache.FileCache;
import com.cmcciot.mat.filemanage.cache.Md5Cache;
import com.cmcciot.mat.filemanage.utils.KeyUtil;
import com.cmcciot.mat.filemanage.utils.PropertyUtil;

public class CacheTest {

	public CacheTest() {
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ParseException {
		setValue("39105ae9e4780fd10fdf14950d178ce", "201406131018040236", "01");
		setValue("39105ae9e4780fd10fdf14950d178ad", "201406130959040236", "02");
		Iterator iter = FileCache.FILEUPLOADCACHEMAP.entrySet().iterator();
		int q = 0;
		while (iter.hasNext()) {
			q++;
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			Md5Cache value = (Md5Cache) entry.getValue();
			String stamptime = value.getTimeTemp();
			String life = PropertyUtil.getValue("life");
			System.out.println("循环顺序：" + q);
			System.out.println(key + "++++++" + value.getMd5() + "++++++" + stamptime);
			if (KeyUtil.compare_date(life, stamptime) < 0) {
				System.out.println("时间戳已经过期");
				iter.remove();
			}
		}
		System.out.println(FileCache.FILEUPLOADCACHEMAP.size());

	}

	public static void setValue(String a, String b, String contentID) {
		Md5Cache aaa = new Md5Cache();
		aaa.setMd5(a);
		aaa.setTimeTemp(b);
		FileCache.FILEUPLOADCACHEMAP.put(contentID, aaa);

	}

}
