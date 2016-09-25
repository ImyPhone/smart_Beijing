package com.itcast.smartbj.newstpipage;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itcast.smartbj.R;
import com.itcast.smartbj.activity.MainActivity;
import com.itcast.smartbj.activity.NewsDetailAcitivity;
import com.itcast.smartbj.domain.NewsCenterData.NewsData.ViewTagData;
import com.itcast.smartbj.domain.TPINewsData;
import com.itcast.smartbj.domain.TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData;
import com.itcast.smartbj.domain.TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData;
import com.itcast.smartbj.utils.MyConstants;
import com.itcast.smartbj.utils.SpTools;
import com.itcast.smartbj.view.RefreshListView;
import com.itcast.smartbj.view.RefreshListView.OnRefreshDataListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TPINewsNewsCenterPager {
	// �������
	@ViewInject(R.id.vp_tpi_news_lunbopic)
	private ViewPager vp_lunbo;
	@ViewInject(R.id.tv_tpi_news_picdesc)
	private TextView tv_pic_desc;
	@ViewInject(R.id.ll_tpi_news_points)
	private LinearLayout ll_points;
	@ViewInject(R.id.lv_tpi_news_listnews)
	private RefreshListView lv_listnews;

	// �������
	private MainActivity mainActivity;
	private View root;
	private ViewTagData viewTagData;
	private Gson gson;
	// �ֲ�ͼ������
	private List<TPINewsData_Data_LunBoData> lunboDatas = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData>();
	// �ֲ�ͼ��������
	private LunBoAdapter lunBoAdapter;
	// listview������
	private List<TPINewsData_Data_ListNewsData> listNewsDatas = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData>();
	// listview����������
	private ListNewsAdapter listNewsAdapter;

	private String loadingMoreDateUrl;
	private String loadingDateUrl;

	private BitmapUtils bitmapUtils;
	private int picSelectIndex;
	private Handler handler;
	private LunBoTask lunBoTask;

	private boolean isFresh = false;

	public TPINewsNewsCenterPager(MainActivity mainActivity,
			ViewTagData viewTagData) {
		this.mainActivity = mainActivity;
		this.viewTagData = viewTagData;
		gson = new Gson();
		handler = new Handler();
		lunBoTask = new LunBoTask();
		// xutils��bitmap ���
		bitmapUtils = new BitmapUtils(mainActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		initView();
		initData();
		initEvent();
	}

	private void initData() {
		// �ֲ�ͼ������
		lunBoAdapter = new LunBoAdapter();
		vp_lunbo.setAdapter(lunBoAdapter);

		// �����б�������
		listNewsAdapter = new ListNewsAdapter();
		lv_listnews.setAdapter(listNewsAdapter);

		// ���ػ�ȡ
		String jsonCache = SpTools.getString(mainActivity, loadingDateUrl,
				null);
		if (!TextUtils.isEmpty(jsonCache)) {
			// ��������
			TPINewsData newsData = parseJson(jsonCache);
			// ��������
			processData(newsData);
		}
		
		loadingDateUrl = MyConstants.SERVERURL + viewTagData.url;
		getDataFromNet(loadingDateUrl,false);// �����ȡ

	}

	private void processData(TPINewsData newsData) {
		// ������ݵĴ���
		// 1.�����ֲ�ͼ������
		setLunBoData(newsData);

		// 2.�ֲ�ͼ��Ӧ�ĵ㴦��
		initPoints();

		// 3.����ͼƬ�����͵��ѡ��Ч��
		setPicDescAndPointSelect(picSelectIndex);

		// 4.��ʼ�ֲ�ͼ
		lunBoTask.startLunbo();

		// 5.�����б������
		setListViewNews(newsData);
	}

	// ���������б������
	private void setListViewNews(TPINewsData newsData) {
		listNewsDatas = newsData.data.news;
		// ���½���
		listNewsAdapter.notifyDataSetChanged();

	}

	private class ListNewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return listNewsDatas.size();
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
			ViewHolder vHolder = null;
			if (convertView == null) {
				convertView = View.inflate(mainActivity,
						R.layout.tpi_news_listview_item, null);
				vHolder = new ViewHolder();
				vHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_tpi_news_listview_item_icon);
				vHolder.iv_newspic = (ImageView) convertView
						.findViewById(R.id.iv_tpi_news_listview_item_pic);
				vHolder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_tpi_news_listview_item_title);
				vHolder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_tpi_news_listview_item_time);
				convertView.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}
			
			
			TPINewsData_Data_ListNewsData tpiNewsData_Data_ListNewsData = listNewsDatas.get(position);
			
			//�жϸ������Ƿ��ȡ��
			String newsId = tpiNewsData_Data_ListNewsData.id;
			String readnewsIDs = SpTools.getString(mainActivity, MyConstants.READNESIDS, "");
			if (TextUtils.isEmpty(readnewsIDs) || !readnewsIDs.contains(newsId)) {
				//�� û�б����id
				vHolder.tv_title.setTextColor(Color.BLACK);
				vHolder.tv_time.setTextColor(Color.BLACK);
			}else {
				vHolder.tv_title.setTextColor(Color.GRAY);
				vHolder.tv_time.setTextColor(Color.GRAY);
			}
			
			// ���ñ���
			vHolder.tv_title.setText(tpiNewsData_Data_ListNewsData.title);
			// ����ʱ��
			vHolder.tv_time.setText(tpiNewsData_Data_ListNewsData.pubdate);
			// ����ͼƬ
			bitmapUtils.display(vHolder.iv_newspic,
					tpiNewsData_Data_ListNewsData.listimage);

			return convertView;
		}

	}

	private class ViewHolder {
		ImageView iv_newspic;
		TextView tv_title;
		TextView tv_time;
		ImageView iv_icon;
	}

	private void lunboProcess() {
		// ���ԭ�����е�����
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// ���񣺿����ֲ�ͼ����ʾ
				vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1)
						% vp_lunbo.getAdapter().getCount());
				handler.postDelayed(this, 3000);

			}
		}, 3000);

	}

	private class LunBoTask extends Handler implements Runnable {

		public void stopLunbo() {
			// �Ƴ���ǰ���е�����
			removeCallbacksAndMessages(null);
		}

		public void startLunbo() {
			stopLunbo();
			postDelayed(this, 2000);
		}

		@Override
		public void run() {
			vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1)
					% vp_lunbo.getAdapter().getCount());
			postDelayed(this, 2000);
		}

	}

	private void setPicDescAndPointSelect(int picSelectIndex) {
		
		if (picSelectIndex < 0 || picSelectIndex > lunboDatas.size() - 1){
			return ;
		}
		// ����������Ϣ
		tv_pic_desc.setText(lunboDatas.get(picSelectIndex).title);

		// ���õ��Ƿ���ѡ�е�
		for (int i = 0; i < lunboDatas.size(); i++) {
			ll_points.getChildAt(i).setEnabled(i == picSelectIndex);
		}

	}

	private void initPoints() {
		ll_points.removeAllViews();// ��Ϊ�������ݺ��������ݶ�Ҫ�����÷���ִ��������
		// �ֲ�ͼ�м��� �ͼӼ�����
		for (int i = 0; i < lunboDatas.size(); i++) {
			View v_point = new View(mainActivity);
			v_point.setBackgroundResource(R.drawable.point_selector);
			v_point.setEnabled(false);// Ĭ�϶��ǻҵ�
			LayoutParams params = new LayoutParams(5, 5); // ���С
			params.leftMargin = 10;// ����
			v_point.setLayoutParams(params);
			ll_points.addView(v_point);
		}
	}

	private void setLunBoData(TPINewsData newsData) {
		// ��ȡ�ֲ�ͼ������
		lunboDatas = newsData.data.topnews;
		lunBoAdapter.notifyDataSetChanged();
	}

	private class LunBoAdapter extends PagerAdapter {

		@Override
		public int getCount() {

			return lunboDatas.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv_lunbo_pic = new ImageView(mainActivity);
			iv_lunbo_pic.setScaleType(ScaleType.FIT_XY);
			// ����Ĭ�ϵ�ͼƬ ������
			iv_lunbo_pic.setImageResource(R.drawable.home_scroll_default);
			String topPictureUrl = lunboDatas.get(position).topimage;
			// �첽����ͼƬ �����������ʾ
			bitmapUtils.display(iv_lunbo_pic, topPictureUrl);
			// ��ͼƬ��Ӵ����¼�
			iv_lunbo_pic.setOnTouchListener(new OnTouchListener() {

				private float downY;
				private float downX;
				private long downTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // ���� ֹͣ�ֲ�
						lunBoTask.stopLunbo();
						downX = event.getX();
						downY = event.getY();
						downTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP: // �ɿ�
						lunBoTask.startLunbo();
						float upX = event.getX();
						float upY = event.getY();
						if (upX == downX && upY == downY) {
							long upTime = System.currentTimeMillis();
							if (upTime - downTime < 500) {
								lunboPicClick("ͼƬ�������,�Ǻ���!!!");
							}
						}
						break;
					case MotionEvent.ACTION_CANCEL:// �¼�ȡ��
						lunBoTask.startLunbo();
						break;
					default:
						break;
					}
					return true;
				}

				private void lunboPicClick(Object object) {
					// ���ͼƬ�ĵ����¼�
					System.out.println((String) object);

				}
			});
			container.addView(iv_lunbo_pic);
			return iv_lunbo_pic;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	private TPINewsData parseJson(String jsonData) {
		TPINewsData tpiNewsData = gson.fromJson(jsonData, TPINewsData.class);
		if (!TextUtils.isEmpty(tpiNewsData.data.more)) {
			loadingMoreDateUrl = MyConstants.SERVERURL + tpiNewsData.data.more;
		}else {
			loadingMoreDateUrl = "";
		}
		// System.out.println(tpiNewsData.data.news.get(0).title);
		return tpiNewsData;
	}

	private void getDataFromNet(final String url, final boolean isLoadingMore) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String jsonData = responseInfo.result;
						// �������ݵ�����
						SpTools.setString(mainActivity, url,
								jsonData);
						// ��������
						TPINewsData newsData = parseJson(jsonData);
						
						//�ж��Ƿ��Ǽ��ظ��������
						if (isLoadingMore) {
							//ԭ�е�����+������
							listNewsDatas.addAll(newsData.data.news);
							//���½���
							listNewsAdapter.notifyDataSetChanged();
							Toast.makeText(mainActivity, "��ȡ�������ݳɹ�",
									Toast.LENGTH_SHORT).show();
							
						}else {
							// ��������
							processData(newsData);

							if (isFresh) {
								SystemClock.sleep(1000);
								Toast.makeText(mainActivity, "�������ݳɹ�",
										Toast.LENGTH_SHORT).show();
							}
						}
						lv_listnews.refreshDataFinish();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (isFresh) {
							Toast.makeText(mainActivity, "��������ʧ��",
									Toast.LENGTH_SHORT).show();
							lv_listnews.refreshDataFinish();
						}
					}
				});

	}

	private void initEvent() {
		
		lv_listnews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��������
				TPINewsData_Data_ListNewsData tpiNewsData_Data_ListNewsData = listNewsDatas.get(position-1);
				String newsurl = tpiNewsData_Data_ListNewsData.url;
								
				//����id
				String newsid = tpiNewsData_Data_ListNewsData.id;
				String readIDs = SpTools.getString(mainActivity, MyConstants.READNESIDS, null);
				if (TextUtils.isEmpty(readIDs)) {
					readIDs =newsid;
				}else {
					readIDs += ","+newsid;
				}
				SpTools.setString(mainActivity, MyConstants.READNESIDS, readIDs);
				
				//���߽������
				listNewsAdapter.notifyDataSetChanged();
				
				//��ת������ҳ��
				Intent newsActivity = new Intent(mainActivity,NewsDetailAcitivity.class);
				newsActivity.putExtra("newsurl", newsurl);
				mainActivity.startActivity(newsActivity); 
			}
		});
		
		lv_listnews.setOnRefreshDataListener(new OnRefreshDataListener() {
			
			@Override
			public void refreshData() {
				isFresh  = true;
				getDataFromNet(MyConstants.SERVERURL + viewTagData.url,false);
				//�ı�listview״̬	
			}

			@Override
			public void loadingMore() {
				//�ж��Ƿ��и��������
				if (TextUtils.isEmpty(loadingMoreDateUrl)) {
					Toast.makeText(mainActivity, "û�и��������...", 0).show();
					//�ر�ˢ�����ݵ�״̬
					lv_listnews.refreshDataFinish();
				}else {
					getDataFromNet(loadingMoreDateUrl,true);
				}
				
			}
			
		});
		
		vp_lunbo.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				picSelectIndex = position;
				setPicDescAndPointSelect(picSelectIndex);
				
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

	private void initView() {
		root = View.inflate(mainActivity, R.layout.tpi_news_content, null);
		ViewUtils.inject(this, root);

		View lunboPicView = View.inflate(mainActivity,
				R.layout.tpi_news_lunbopic, null);
		ViewUtils.inject(this, lunboPicView);

		lv_listnews.setIsRefreshHead(true);
		lv_listnews.addHeaderView(lunboPicView);

		// ���ڸ���������ֻ��listview��listview������lunboPicView

	}

	public View getRootView() {
		return root;
	}
}
