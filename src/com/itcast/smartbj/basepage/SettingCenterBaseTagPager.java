package com.itcast.smartbj.basepage;

import com.itcast.smartbj.activity.MainActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;



public class SettingCenterBaseTagPager extends BaseTagPage {

	public SettingCenterBaseTagPager(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initData() {
		ib_menu.setVisibility(View.GONE);
		tv_title.setText("��������");	
		TextView tv = new TextView(mainActivity);
		tv.setText("�������ĵ�����");
		tv.setTextSize(26);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
