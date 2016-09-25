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
		//切换到下一个页面
		vp_newscenter.setCurrentItem(vp_newscenter.getCurrentItem()+1);
	}

	private List<ViewTagData> mViewTagDatas = new ArrayList<NewsCenterData.NewsData.ViewTagData>();// 页签的数据

	public NewsBaseNewsCenterPage(MainActivity mainActivity,
			List<ViewTagData> children) {
		super(mainActivity);
		this.mViewTagDatas = children;
	}

	@Override
	public View initView() {
		View newsCenterRoot = View.inflate(mainActivity,
				R.layout.newscenterpage_content, null);
		// 通过工具注入组件
		ViewUtils.inject(this, newsCenterRoot);
		return newsCenterRoot;
	}

	@Override
	public void initData() {
		// 设置数据
		MyAdapter adapter = new MyAdapter();

		// 设置ViewPager的适配器
		vp_newscenter.setAdapter(adapter);

		// 把ViewPager和Tabpageindicator关联
		tpi_newscenter.setViewPager(vp_newscenter);
		
	}
	
	@Override
	public void initEvent() {
//		vp_newscenter.setOnPageChangeListener(new OnPageChangeListener())
//		viewPager监听事件，当页面位于第一可以滑动出坐菜单,但是TabPageIndicator做了一件事
//		if (mViewPager != null) {
//			mViewPager.setOnPageChangeListener(null);
//		}  所以：
				
		tpi_newscenter.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// 当页面位于第一可以滑动出做菜单
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

	// 页签对应viewpager的适配器
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
		
		//页签显示数据调用该方法
		@Override
		public CharSequence getPageTitle(int position) {
			//获取页签的数据
			return mViewTagDatas.get(position).title;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
		/*	TextView tv = new TextView(mainActivity);
			tv.setText(mViewTagDatas.get(position).title);
			tv.setGravity(Gravity.CENTER);
			container.addView(tv);*/
			
			TPINewsNewsCenterPager tpiPager = new TPINewsNewsCenterPager(mainActivity,mViewTagDatas.get(position));
			//北京 中国 国际 体育 生活 旅游 军事 科技....等多个TPINewsNewsCenterPager对象生成
			View rootView = tpiPager.getRootView();
			container.addView(rootView);
			return rootView;
		}
		
		

	}

}
