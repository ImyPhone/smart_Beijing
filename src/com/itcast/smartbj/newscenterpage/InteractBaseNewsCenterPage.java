package com.itcast.smartbj.newscenterpage;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itcast.smartbj.activity.MainActivity;

public class InteractBaseNewsCenterPage extends BaseNewsCenterPage{

	public InteractBaseNewsCenterPage(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mainActivity);
		tv.setText("»¥¶¯");
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

}
