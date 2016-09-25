

package com.itcast.smartbj.domain;

import java.util.List;



public class PhotosData
{

	public int				retcode;
	public PhotosData_Data	data;

	public class PhotosData_Data
	{
		public String			countcommenturl;
		public String			more;
		public String			title;

		public List<PhotosNews>	news;

		public class PhotosNews
		{
			public boolean comment;
			public String commentlist;
			public String commenturl;
			public int id;	
			public String largeimage;
			public String listimage;
			public String pubdate;
			public String smallimage;
			public String title;
			public String type;
			public String url;
		}
	}
}
