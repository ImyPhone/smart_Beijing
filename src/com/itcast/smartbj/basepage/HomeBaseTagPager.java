package com.itcast.smartbj.basepage;

import com.itcast.smartbj.activity.MainActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;



public class HomeBaseTagPager extends BaseTagPage {

	public HomeBaseTagPager(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initData() {
		ib_menu.setVisibility(View.GONE);
		tv_title.setText("Ê×Ò³");	
		TextView tv = new TextView(mainActivity);
		tv.setText("Ê×Ò³ÄÚÈÝ");
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
	
}
