package com.itcast.smartbj.view;

import java.util.ArrayList;
import java.util.List;

import com.itcast.smartbj.R;
import com.itcast.smartbj.basepage.BaseTagPage;
import com.itcast.smartbj.basepage.GovAffairsBaseTagPager;
import com.itcast.smartbj.basepage.HomeBaseTagPager;
import com.itcast.smartbj.basepage.NewsCenterBaseTagPager;
import com.itcast.smartbj.basepage.SettingCenterBaseTagPager;
import com.itcast.smartbj.basepage.SmartServiceBaseTagPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainContentFragment extends BaseFragment {
	@ViewInject(R.id.vp_main_content_pages)
	private ViewPager mViewPager;

	@ViewInject(R.id.rg_content_radios)
	private RadioGroup mRadioGroup;

	private List<BaseTagPage> pages = new ArrayList<BaseTagPage>();

	private int selectIndex;// 设置当前选择的页面编号

	private ImageButton ib_menu;

	@Override
	public View initView() {
		View root = View.inflate(mainActivity, R.layout.fragment_content_view,
				null);
		// 动态注入view
		ViewUtils.inject(this, root);
		return root;
	}

	@Override
	public void initData() {
		pages.add(new HomeBaseTagPager(mainActivity)); // mainActivity上下文传递进去作用为给root->View
		pages.add(new NewsCenterBaseTagPager(mainActivity));// 作用二：view的ib_menu需要设置开关菜单的点击事件->mainActivity.getSlidingMenu().toggle()
		pages.add(new SmartServiceBaseTagPager(mainActivity));
		pages.add(new GovAffairsBaseTagPager(mainActivity));
		pages.add(new SettingCenterBaseTagPager(mainActivity));

		MyAdapter adapter = new MyAdapter();
		mViewPager.setAdapter(adapter);
		// mViewPager.setOffscreenPageLimit(2); 前后预加载两个页面
		// 用低版本的ViewPager通过修改private static final int DEFAULT_OFFSCREEN_PAGES =
		// 1的值为0，可以懒加载

		// 一开始就要显示的页面
		switchPage();
		// 一开始选中home按钮
		mRadioGroup.check(R.id.rb_main_content_home);
	}

	@Override
	public void initEvent() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_main_content_home:
					selectIndex = 0;
					break;
				case R.id.rb_main_content_newscenter:
					selectIndex = 1;
					break;
				case R.id.rb_main_content_smartservice:
					selectIndex = 2;
					break;
				case R.id.rb_main_content_govaffairs:
					selectIndex = 3;
					break;
				case R.id.rb_main_content_settingcenter:
					selectIndex = 4;
					break;
				}
				switchPage(); // 点击哪个页面跳到哪个页面
			}
		});
		super.initEvent();
	}

	// 设置选中页面
	private void switchPage() {
		mViewPager.setCurrentItem(selectIndex);
		if (selectIndex == 0 || selectIndex == pages.size() - 1) {
			mainActivity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_NONE);
		} else {
			mainActivity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_FULLSCREEN);// 屏幕任何位置都可以滑动出
		}
	}

	
	// 点击左菜单，让page切换不同的页面
	public void leftMenuClickSwitchPage(int subSelectionIndex) {
		//只有MainContentFragment(的RadioGroup)才知道用户选择了哪个page
		BaseTagPage baseTagPage = pages.get(selectIndex);
		
		//page才可以调用switchPage(subSelectionIndex)方法选择哪个页面
		//subSelectionIndex这个数据由LeftMenuFragment的ListView的ItemClickListener提供
		baseTagPage.switchPage(subSelectionIndex);
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pages.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BaseTagPage baseTagPage = pages.get(position);
			baseTagPage.initData();// 加载数据 每个baseTagPage子类都复写了initData()方法
			View root = baseTagPage.getRoot(); // BaseTagPage不是个view对象
			container.addView(root); // root->((menu title
										// Framelayout)在BaseTagPage就被填充为View对象)
			return root;// 等于说viewpager显示的是NewsCenterBaseTagPager...的root
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
