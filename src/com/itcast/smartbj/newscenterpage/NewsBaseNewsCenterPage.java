package com.itcast.smartbj.newscenterpage;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import com.itcast.smartbj.R;
import com.itcast.smartbj.activity.MainActivity;
import com.itcast.smartbj.domain.NewsCenterData;
import com.itcast.smartbj.domain.NewsCenterData.NewsData.ViewTagData;
import com.itcast.smartbj.newstpipage.TPINewsNewsCenterPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

public class NewsBaseNewsCenterPage extends BaseNewsCenterPage {
	@ViewInject(R.id.newscenter_vp)
	private ViewPager vp_newscenter;
	@ViewInject(R.id.newcenter_tpi)
	private TabPageIndicator tpi_newscenter;
	
	@OnClick(R.id.newscenter_ib_nextpage)
	public void next(View view){
		//�л�����һ��ҳ��
		vp_newscenter.setCurrentItem(vp_newscenter.getCurrentItem()+1);
	}

	private List<ViewTagData> mViewTagDatas = new ArrayList<NewsCenterData.NewsData.ViewTagData>();// ҳǩ������

	public NewsBaseNewsCenterPage(MainActivity mainActivity,
			List<ViewTagData> children) {
		super(mainActivity);
		this.mViewTagDatas = children;
	}

	@Override
	public View initView() {
		View newsCenterRoot = View.inflate(mainActivity,
				R.layout.newscenterpage_content, null);
		// ͨ������ע�����
		ViewUtils.inject(this, newsCenterRoot);
		return newsCenterRoot;
	}

	@Override
	public void initData() {
		// ��������
		MyAdapter adapter = new MyAdapter();

		// ����ViewPager��������
		vp_newscenter.setAdapter(adapter);

		// ��ViewPager��Tabpageindicator����
		tpi_newscenter.setViewPager(vp_newscenter);
		
	}
	
	@Override
	public void initEvent() {
//		vp_newscenter.setOnPageChangeListener(new OnPageChangeListener())
//		viewPager�����¼�����ҳ��λ�ڵ�һ���Ի��������˵�,����TabPageIndicator����һ����
//		if (mViewPager != null) {
//			mViewPager.setOnPageChangeListener(null);
//		}  ���ԣ�
				
		tpi_newscenter.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// ��ҳ��λ�ڵ�һ���Ի��������˵�
				if (arg0 == 0) {
					mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}else {
					mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	// ҳǩ��Ӧviewpager��������
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViewTagDatas.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			
		}
		
		//ҳǩ��ʾ���ݵ��ø÷���
		@Override
		public CharSequence getPageTitle(int position) {
			//��ȡҳǩ������
			return mViewTagDatas.get(position).title;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
		/*	TextView tv = new TextView(mainActivity);
			tv.setText(mViewTagDatas.get(position).title);
			tv.setGravity(Gravity.CENTER);
			container.addView(tv);*/
			
			TPINewsNewsCenterPager tpiPager = new TPINewsNewsCenterPager(mainActivity,mViewTagDatas.get(position));
			//���� �й� ���� ���� ���� ���� ���� �Ƽ�....�ȶ��TPINewsNewsCenterPager��������
			View rootView = tpiPager.getRootView();
			container.addView(rootView);
			return rootView;
		}
		
		

	}

}
