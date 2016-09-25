package com.itcast.smartbj.basepage;

import com.itcast.smartbj.activity.MainActivity;

import android.view.Gravity;
import android.widget.TextView;



public class GovAffairsBaseTagPager extends BaseTagPage {

	public GovAffairsBaseTagPager(MainActivity mainActivity) {		
		super(mainActivity);
		
	}
	@Override
	public void initData() {
		tv_title.setText("����");	
		TextView tv = new TextView(mainActivity);
		tv.setText("��������");
		tv.setTextSize(26);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
	
}
