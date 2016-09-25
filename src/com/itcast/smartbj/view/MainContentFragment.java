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

	private int selectIndex;// ���õ�ǰѡ���ҳ����

	private ImageButton ib_menu;

	@Override
	public View initView() {
		View root = View.inflate(mainActivity, R.layout.fragment_content_view,
				null);
		// ��̬ע��view
		ViewUtils.inject(this, root);
		return root;
	}

	@Override
	public void initData() {
		pages.add(new HomeBaseTagPager(mainActivity)); // mainActivity�����Ĵ��ݽ�ȥ����Ϊ��root->View
		pages.add(new NewsCenterBaseTagPager(mainActivity));// ���ö���view��ib_menu��Ҫ���ÿ��ز˵��ĵ���¼�->mainActivity.getSlidingMenu().toggle()
		pages.add(new SmartServiceBaseTagPager(mainActivity));
		pages.add(new GovAffairsBaseTagPager(mainActivity));
		pages.add(new SettingCenterBaseTagPager(mainActivity));

		MyAdapter adapter = new MyAdapter();
		mViewPager.setAdapter(adapter);
		// mViewPager.setOffscreenPageLimit(2); ǰ��Ԥ��������ҳ��
		// �õͰ汾��ViewPagerͨ���޸�private static final int DEFAULT_OFFSCREEN_PAGES =
		// 1��ֵΪ0������������

		// һ��ʼ��Ҫ��ʾ��ҳ��
		switchPage();
		// һ��ʼѡ��home��ť
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
				switchPage(); // ����ĸ�ҳ�������ĸ�ҳ��
			}
		});
		super.initEvent();
	}

	// ����ѡ��ҳ��
	private void switchPage() {
		mViewPager.setCurrentItem(selectIndex);
		if (selectIndex == 0 || selectIndex == pages.size() - 1) {
			mainActivity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_NONE);
		} else {
			mainActivity.getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_FULLSCREEN);// ��Ļ�κ�λ�ö����Ի�����
		}
	}

	
	// �����˵�����page�л���ͬ��ҳ��
	public void leftMenuClickSwitchPage(int subSelectionIndex) {
		//ֻ��MainContentFragment(��RadioGroup)��֪���û�ѡ�����ĸ�page
		BaseTagPage baseTagPage = pages.get(selectIndex);
		
		//page�ſ��Ե���switchPage(subSelectionIndex)����ѡ���ĸ�ҳ��
		//subSelectionIndex���������LeftMenuFragment��ListView��ItemClickListener�ṩ
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
			baseTagPage.initData();// �������� ÿ��baseTagPage���඼��д��initData()����
			View root = baseTagPage.getRoot(); // BaseTagPage���Ǹ�view����
			container.addView(root); // root->((menu title
										// Framelayout)��BaseTagPage�ͱ����ΪView����)
			return root;// ����˵viewpager��ʾ����NewsCenterBaseTagPager...��root
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
