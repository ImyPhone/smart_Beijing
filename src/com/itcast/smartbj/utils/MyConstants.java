package com.itcast.smartbj.utils;

public interface MyConstants {
	String SERVERURL = "http://10.0.2.2:8080/zhbj";
	String PHOTOURL = SERVERURL+"/photos/photos_1.json";
	String NEWSCENTERURL = "http://10.0.2.2:8080/zhbj/categories.json";	
	String MYCONFIG ="config"; //缓存数据的文件名  
	String READNESIDS = "readnewsids";//保存读过的新闻id
}
