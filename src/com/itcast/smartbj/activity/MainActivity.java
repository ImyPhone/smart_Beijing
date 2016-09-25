package com.itcast.smartbj.activity;

import com.itcast.smartbj.R;
import com.itcast.smartbj.view.LeftMenuFragment;
import com.itcast.smartbj.view.MainContentFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;



public class MainActivity extends SlidingFragmentActivity {

    private static final String LEFT_MENU_TAG = "left_menu_tag";
	private static final String MAIN_MENU_TAG = "main_menu_tag";

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        initData();
        
    }
	
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager manager = getSupportFragmentManager();
		LeftMenuFragment leftMenuFragment= (LeftMenuFragment) manager.findFragmentByTag(LEFT_MENU_TAG);
		return leftMenuFragment;
	}
	
	public MainContentFragment getMainContentFragment() {
		FragmentManager manager = getSupportFragmentManager();
		MainContentFragment mainContentFragmentt= (MainContentFragment) manager.findFragmentByTag(MAIN_MENU_TAG);
		return mainContentFragmentt;
	}

	private void initData() {
		//��ȡ����
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		//����滻                                       ������˵�������л�
		transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), LEFT_MENU_TAG);
		//									������
		transaction.replace(R.id.fl_main_menu, new MainContentFragment(), MAIN_MENU_TAG);
		
		//�ύ����
		transaction.commit();
		
		
	}

	private void initView() {
		setContentView(R.layout.fragment_content);   //�����沼��
		setBehindContentView(R.layout.fragment_left);//��˵�����
		SlidingMenu menu = getSlidingMenu();
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setMode(SlidingMenu.LEFT);
		menu.setBehindOffset(150); //������ʣ��λ��
		
		
	}   
}
