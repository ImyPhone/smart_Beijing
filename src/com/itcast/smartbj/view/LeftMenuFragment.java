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
	
	private List<NewsData> data = new ArrayList<NewsCenterData.NewsData>(); // initData �����������˵�����
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
				//����ѡ�е�λ��
				selectPosition = position;
				
				//���½���
				adapter.notifyDataSetChanged();
				
				//�������������ĸ�ҳ�����ʾ
				if (mListener != null) {
					//����һ��
					mListener.switchPage(selectPosition);
				}else {
					//��������
					 mainActivity.getMainContentFragment().leftMenuClickSwitchPage(selectPosition);
				}
				
				//�л�SlidingMenu�Ŀ���
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
			lv_leftData.setCacheColorHint(Color.TRANSPARENT);//ѡ���϶��ı���ɫ ͸��
			
			//lv_leftData.setSelector(Color.TRANSPARENT);ѡ��ʱ͸������
			lv_leftData.setSelector(new ColorDrawable(Color.TRANSPARENT));
			
			lv_leftData.setDividerHeight(0);//�ָ���0 û�зָ���
			lv_leftData.setPadding(0, 45, 0, 0);//���붥��45����
			
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
			
			//�ж��Ƿ�ѡ��
			tv.setEnabled(position == selectPosition);
			return tv;
		}
		
	}
}
