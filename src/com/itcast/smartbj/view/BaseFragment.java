package com.itcast.smartbj.view;

import com.itcast.smartbj.activity.MainActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	protected MainActivity mainActivity;//获取上下文
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();//获取fragment所在Activity
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = initView();
		return view;
	}
	
	public abstract View initView();
	
	public void initData() {
		
	}
	
	public void initEvent() {
		
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// 初始化事件和数据
		super.onActivityCreated(savedInstanceState);
		initData();
		initEvent();
	}
}
