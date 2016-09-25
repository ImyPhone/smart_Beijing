package com.itcast.smartbj.activity;

import java.util.ArrayList;
import java.util.List;

import com.itcast.smartbj.R;
import com.itcast.smartbj.utils.MyConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class GuideActivity extends Activity {
	
	private List<ImageView> mDatas;
	private ViewPager mViewPager;
	private Button mGuideButton;
	private SharedPreferences sp;
	private LinearLayout mPonitContainer;
	private View mRedPoint;
	private int distanceOfPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = getSharedPreferences(MyConstants.MYCONFIG,Context.MODE_PRIVATE);
		initView();
		initDatas();
		initEvent();
	}
	private void initEvent() {
		//�����������
		mRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);//this������������ڲ��� new OnGlobalLayoutListener()
				//System.out.println(mPonitContainer.getChildAt(1).getLeft()-mPonitContainer.getChildAt(0).getLeft());
				distanceOfPoint = mPonitContainer.getChildAt(1).getLeft()-mPonitContainer.getChildAt(0).getLeft();
			}
		});
		
		mGuideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//����������   ��������ɱ��
				Editor edit = sp.edit();
				edit.putBoolean("isGuideSetup", true);
				edit.commit();
				Intent mainIntent = new Intent(GuideActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int positon) {
				if (positon == mDatas.size()-1) {
					mGuideButton.setVisibility(View.VISIBLE);
				}else {
					mGuideButton.setVisibility(View.GONE);
				}
				
			}
			
			@Override                                          //ƫ�Ʊ���ֵ                   ƫ������
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
				float redPointOffsetPosition = (position+positionOffset)*distanceOfPoint;
				System.out.println("positionOffset is "+positionOffset+".......position is "+position);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mRedPoint.getLayoutParams();
				params.leftMargin = Math.round(redPointOffsetPosition);  //float��������
				mRedPoint.setLayoutParams(params);
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	private void initDatas() {
		int[] pics ={R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};
		mDatas = new ArrayList<ImageView>();
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
//			iv.setImageResource(pics[i]);
//			iv.setScaleType(ScaleType.FIT_XY);
			iv.setBackgroundResource(pics[i]);
			View point = new View(getApplicationContext());
			point.setBackgroundResource(R.drawable.gray_point);
			LayoutParams params = new LayoutParams(10, 10);  //�˵�λpx ����dp
			if (i != 0) {
				params.leftMargin = 10;
			}
			point.setLayoutParams(params);
			mPonitContainer.addView(point);
			mDatas.add(iv);
		}
		
		//�õ��ĵ�֮�����Ϊ0����Ϊ ��ֻ�Ǽӵ���������û���ڽ����ϲ�����ʾ  ��view.getLeft() view.getWidth()...��Щ��Ҫ������ɺ���еģ�
		//System.out.println(mPonitContainer.getChildAt(1).getLeft()-mPonitContainer.getChildAt(0).getLeft());
		
		//����viewpager����������
		mViewPager.setAdapter(new MyAdapter());
		
	}
	private void initView() {
		setContentView(R.layout.guide);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mGuideButton = (Button) findViewById(R.id.guide_last_button);
		mPonitContainer = (LinearLayout) findViewById(R.id.point_container);
		mRedPoint = findViewById(R.id.red_point);
		
		
	}
	
	private class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			//System.out.println("���ƶ��������˸�����");
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			//System.out.println("destroyItem:position = "+position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = mDatas.get(position);
			container.addView(iv);
			//System.out.println("instantiateItem:position = "+position);
			return iv;
		}
		
	}
}
