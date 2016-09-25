package com.itcast.smartbj.view;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itcast.smartbj.R;
import com.itcast.smartbj.domain.NewsCenterData;
import com.itcast.smartbj.domain.NewsCenterData.NewsData;

public class LeftMenuFragment extends BaseFragment {
	
	private List<NewsData> data = new ArrayList<NewsCenterData.NewsData>(); // initData 新闻中心左侧菜单数据
	private ListView lv_leftData;
	private MyAdapter adapter;
	private int selectPosition;
	private OnSwitchPageListener mListener;
	
	public interface OnSwitchPageListener{
		void switchPage(int selectPosition );
	}
	public void setOnSwitchPageListener(OnSwitchPageListener listener ) {
		this.mListener = listener;
	}
	
	@Override
	public void initEvent() {
		lv_leftData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//保存选中的位置
				selectPosition = position;
				
				//更新界面
				adapter.notifyDataSetChanged();
				
				//控制新闻中心四个页面的显示
				if (mListener != null) {
					//方法一：
					mListener.switchPage(selectPosition);
				}else {
					//方法二：
					 mainActivity.getMainContentFragment().leftMenuClickSwitchPage(selectPosition);
				}
				
				//切换SlidingMenu的开关
				mainActivity.getSlidingMenu().toggle();
			}
		});
		super.initEvent();
	}
	@Override
	public View initView() {
		
			lv_leftData = new ListView(mainActivity);
			//lv_leftData.setBackgroundColor(Color.BLACK);
			lv_leftData.setBackgroundResource(R.drawable.menu_bg);
			lv_leftData.setCacheColorHint(Color.TRANSPARENT);//选中拖动的背景色 透明
			
			//lv_leftData.setSelector(Color.TRANSPARENT);选中时透明背景
			lv_leftData.setSelector(new ColorDrawable(Color.TRANSPARENT));
			
			lv_leftData.setDividerHeight(0);//分隔线0 没有分隔线
			lv_leftData.setPadding(0, 45, 0, 0);//距离顶部45像素
			
			return lv_leftData;		
	}
	
	public void setMenuData(List<NewsData> data) {
		this.data = data;
		adapter.notifyDataSetChanged();
	}
	@Override
	public void initData() {
		adapter = new MyAdapter();
		lv_leftData.setAdapter(adapter);
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = (TextView) View.inflate(mainActivity, R.layout.leftmenu_list_item, null);
			}else{
				tv = (TextView) convertView;
			}
			tv.setText(data.get(position).title);
			
			//判断是否被选中
			tv.setEnabled(position == selectPosition);
			return tv;
		}
		
	}
}
