package com.itcast.smartbj.domain;

import java.util.List;

import android.view.ViewGroup;

public class NewsCenterData {
	public int retcode;
	public List<NewsData> data;
	public class NewsData{
		
		public List<ViewTagData> children;	
		public class ViewTagData{
			public String id;
			public String title;
			public int type;
			public String url;
		}
		
		public int id;
		public String title;
		public int type;
		
		public String url;
		public String url1;
		
		public String dayurl;
		public String excurl;
		
		public String weekurl;
	}
	public List<String> extend;
	

}
