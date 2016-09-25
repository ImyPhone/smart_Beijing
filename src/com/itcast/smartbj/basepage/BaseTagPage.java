package com.itcast.smartbj.basepage;

import com.itcast.smartbj.R;
import com.itcast.smartbj.activity.MainActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class BaseTagPage {
	protected MainActivity mainActivity;
	protected View root;
	protected ImageButton ib_menu;
	protected TextView tv_title;
	protected FrameLayout fl_content;
	protected ImageButton ib_listOrGrid;
	public BaseTagPage(MainActivity mainActivity){
		this.mainActivity = mainActivity;
		initView();//初始化布局
		initEvent();
	}

	public void initEvent() {
		ib_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 打开或关闭菜单
				mainActivity.getSlidingMenu().toggle();  
			}
		});
		
	}

	public void initView() {
		root = View.inflate(mainActivity, R.layout.fragment_content_base_content, null);
		ib_menu = (ImageButton) root.findViewById(R.id.ib_base_content_menu);
		tv_title = (TextView) root.findViewById(R.id.tv_base_content_title);
		fl_content = (FrameLayout) root.findViewById(R.id.fl_base_content_tag);
		ib_listOrGrid = (ImageButton) root.findViewById(R.id.ib_base_content_listorgrid);
	}
	
	//显示的时候在调用
	public void initData () {
		
	}
	
	public void switchPage(int positon){
		
	}
	
	public View getRoot(){
		
		return root;
	}
}
