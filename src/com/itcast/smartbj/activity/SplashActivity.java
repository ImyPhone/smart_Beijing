package com.itcast.smartbj.activity;

import com.itcast.smartbj.R;
import com.itcast.smartbj.utils.MyConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	private ImageView iv_splash;
	private AnimationSet as;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		sp = getSharedPreferences(MyConstants.MYCONFIG, Context.MODE_PRIVATE);
		initView();
		startAnimation();
		initEvent();
	}

	private void initEvent() {
		as.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				boolean isGuideSetup = sp.getBoolean("isGuideSetup", false);
				if (isGuideSetup) {
					//进入主界面
					Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(mainIntent);
				}else {
					//进入设置向导
					Intent guideIntent = new Intent(SplashActivity.this, GuideActivity.class);
					startActivity(guideIntent);
				}
				finish();
			}
		});
		
	}

	private void startAnimation() {
		as = new AnimationSet(false);
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(1800);
		aa.setFillAfter(true);
		as.addAnimation(aa);
		
		RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1800);
		ra.setFillAfter(true);
		as.addAnimation(ra);
		
		//ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 0.5f);// <=> 这种方法是相对自从左上角（0,0）放大
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(1800);
		sa.setFillAfter(true);
		as.addAnimation(sa);
		
		iv_splash.startAnimation(as);
		
	}

	private void initView() {
		iv_splash = (ImageView) findViewById(R.id.iv_splash);
		
	}
}
