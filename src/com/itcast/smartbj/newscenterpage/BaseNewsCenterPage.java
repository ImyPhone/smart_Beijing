package com.itcast.smartbj.newscenterpage;

import android.view.View;

import com.itcast.smartbj.activity.MainActivity;

public abstract class BaseNewsCenterPage {
	protected MainActivity mainActivity;
	protected View root;
	public BaseNewsCenterPage(MainActivity mainActivity){
		this.mainActivity = mainActivity;
		root = initView();
		initEvent();
	}
	
	public abstract View initView();
	
	public View getRoot() {
		return root;
	}
	
	public void initData() {
		
	}
	
	public void initEvent() {
		
	}
}
