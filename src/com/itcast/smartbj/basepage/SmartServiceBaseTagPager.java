package com.itcast.smartbj.basepage;

import com.itcast.smartbj.activity.MainActivity;

import android.view.Gravity;
import android.widget.TextView;



public class SmartServiceBaseTagPager extends BaseTagPage {

	public SmartServiceBaseTagPager(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initData() {
		tv_title.setText("智慧服务");	
		TextView tv = new TextView(mainActivity);
		tv.setText("智慧服务的内容");
		tv.setTextSize(26);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
