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
	// 所有组件
	@ViewInject(R.id.vp_tpi_news_lunbopic)
	private ViewPager vp_lunbo;
	@ViewInject(R.id.tv_tpi_news_picdesc)
	private TextView tv_pic_desc;
	@ViewInject(R.id.ll_tpi_news_points)
	private LinearLayout ll_points;
	@ViewInject(R.id.lv_tpi_news_listnews)
	private RefreshListView lv_listnews;

	// 数据相关
	private MainActivity mainActivity;
	private View root;
	private ViewTagData viewTagData;
	private Gson gson;
	// 轮播图的数据
	private List<TPINewsData_Data_LunBoData> lunboDatas = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunBoData>();
	// 轮播图的适配器
	private LunBoAdapter lunBoAdapter;
	// listview的数据
	private List<TPINewsData_Data_ListNewsData> listNewsDatas = new ArrayList<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData>();
	// listview数据适配器
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
		// xutils的bitmap 组件
		bitmapUtils = new BitmapUtils(mainActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		initView();
		initData();
		initEvent();
	}

	private void initData() {
		// 轮播图适配器
		lunBoAdapter = new LunBoAdapter();
		vp_lunbo.setAdapter(lunBoAdapter);

		// 新闻列表适配器
		listNewsAdapter = new ListNewsAdapter();
		lv_listnews.setAdapter(listNewsAdapter);

		// 本地获取
		String jsonCache = SpTools.getString(mainActivity, loadingDateUrl,
				null);
		if (!TextUtils.isEmpty(jsonCache)) {
			// 解析数据
			TPINewsData newsData = parseJson(jsonCache);
			// 处理数据
			processData(newsData);
		}
		
		loadingDateUrl = MyConstants.SERVERURL + viewTagData.url;
		getDataFromNet(loadingDateUrl,false);// 网络获取

	}

	private void processData(TPINewsData newsData) {
		// 完成数据的处理
		// 1.设置轮播图的数据
		setLunBoData(newsData);

		// 2.轮播图对应的点处理
		initPoints();

		// 3.设置图片描述和点的选中效果
		setPicDescAndPointSelect(picSelectIndex);

		// 4.开始轮播图
		lunBoTask.startLunbo();

		// 5.新闻列表的数据
		setListViewNews(newsData);
	}

	// 设置新闻列表的数据
	private void setListViewNews(TPINewsData newsData) {
		listNewsDatas = newsData.data.news;
		// 更新界面
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
			
			//判断该新闻是否读取过
			String newsId = tpiNewsData_Data_ListNewsData.id;
			String readnewsIDs = SpTools.getString(mainActivity, MyConstants.READNESIDS, "");
			if (TextUtils.isEmpty(readnewsIDs) || !readnewsIDs.contains(newsId)) {
				//空 没有保存过id
				vHolder.tv_title.setTextColor(Color.BLACK);
				vHolder.tv_time.setTextColor(Color.BLACK);
			}else {
				vHolder.tv_title.setTextColor(Color.GRAY);
				vHolder.tv_time.setTextColor(Color.GRAY);
			}
			
			// 设置标题
			vHolder.tv_title.setText(tpiNewsData_Data_ListNewsData.title);
			// 设置时间
			vHolder.tv_time.setText(tpiNewsData_Data_ListNewsData.pubdate);
			// 设置图片
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
		// 清空原来所有的任务
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 任务：控制轮播图的显示
				vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1)
						% vp_lunbo.getAdapter().getCount());
				handler.postDelayed(this, 3000);

			}
		}, 3000);

	}

	private class LunBoTask extends Handler implements Runnable {

		public void stopLunbo() {
			// 移除当前所有的任务
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
		// 设置描述信息
		tv_pic_desc.setText(lunboDatas.get(picSelectIndex).title);

		// 设置点是否是选中的
		for (int i = 0; i < lunboDatas.size(); i++) {
			ll_points.getChildAt(i).setEnabled(i == picSelectIndex);
		}

	}

	private void initPoints() {
		ll_points.removeAllViews();// 因为本地数据和网络数据都要处理，该方法执行两次了
		// 轮播图有几个 就加几个点
		for (int i = 0; i < lunboDatas.size(); i++) {
			View v_point = new View(mainActivity);
			v_point.setBackgroundResource(R.drawable.point_selector);
			v_point.setEnabled(false);// 默认都是灰点
			LayoutParams params = new LayoutParams(5, 5); // 点大小
			params.leftMargin = 10;// 点间距
			v_point.setLayoutParams(params);
			ll_points.addView(v_point);
		}
	}

	private void setLunBoData(TPINewsData newsData) {
		// 获取轮播图的数据
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
			// 设置默认的图片 网速慢
			iv_lunbo_pic.setImageResource(R.drawable.home_scroll_default);
			String topPictureUrl = lunboDatas.get(position).topimage;
			// 异步加载图片 并在组件中显示
			bitmapUtils.display(iv_lunbo_pic, topPictureUrl);
			// 给图片添加触摸事件
			iv_lunbo_pic.setOnTouchListener(new OnTouchListener() {

				private float downY;
				private float downX;
				private long downTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // 按下 停止轮播
						lunBoTask.stopLunbo();
						downX = event.getX();
						downY = event.getY();
						downTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP: // 松开
						lunBoTask.startLunbo();
						float upX = event.getX();
						float upY = event.getY();
						if (upX == downX && upY == downY) {
							long upTime = System.currentTimeMillis();
							if (upTime - downTime < 500) {
								lunboPicClick("图片被点击啦,呵呵哒!!!");
							}
						}
						break;
					case MotionEvent.ACTION_CANCEL:// 事件取消
						lunBoTask.startLunbo();
						break;
					default:
						break;
					}
					return true;
				}

				private void lunboPicClick(Object object) {
					// 点击图片的单机事件
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
						// 保存数据到本地
						SpTools.setString(mainActivity, url,
								jsonData);
						// 解析数据
						TPINewsData newsData = parseJson(jsonData);
						
						//判断是否是加载更多的数据
						if (isLoadingMore) {
							//原有的数据+新数据
							listNewsDatas.addAll(newsData.data.news);
							//更新界面
							listNewsAdapter.notifyDataSetChanged();
							Toast.makeText(mainActivity, "获取更多数据成功",
									Toast.LENGTH_SHORT).show();
							
						}else {
							// 处理数据
							processData(newsData);

							if (isFresh) {
								SystemClock.sleep(1000);
								Toast.makeText(mainActivity, "加载数据成功",
										Toast.LENGTH_SHORT).show();
							}
						}
						lv_listnews.refreshDataFinish();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (isFresh) {
							Toast.makeText(mainActivity, "加载数据失败",
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
				//新闻链接
				TPINewsData_Data_ListNewsData tpiNewsData_Data_ListNewsData = listNewsDatas.get(position-1);
				String newsurl = tpiNewsData_Data_ListNewsData.url;
								
				//新闻id
				String newsid = tpiNewsData_Data_ListNewsData.id;
				String readIDs = SpTools.getString(mainActivity, MyConstants.READNESIDS, null);
				if (TextUtils.isEmpty(readIDs)) {
					readIDs =newsid;
				}else {
					readIDs += ","+newsid;
				}
				SpTools.setString(mainActivity, MyConstants.READNESIDS, readIDs);
				
				//告诉界面跟新
				listNewsAdapter.notifyDataSetChanged();
				
				//跳转到新闻页面
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
				//改变listview状态	
			}

			@Override
			public void loadingMore() {
				//判断是否有更多的数据
				if (TextUtils.isEmpty(loadingMoreDateUrl)) {
					Toast.makeText(mainActivity, "没有更多多数据...", 0).show();
					//关闭刷新数据的状态
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

		// 等于根布局里面只有listview，listview里面有lunboPicView

	}

	public View getRootView() {
		return root;
	}
}
