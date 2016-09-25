package com.itcast.smartbj.newscenterpage;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itcast.smartbj.R;
import com.itcast.smartbj.activity.MainActivity;
import com.itcast.smartbj.domain.PhotosData;
import com.itcast.smartbj.domain.PhotosData.PhotosData_Data.PhotosNews;
import com.itcast.smartbj.utils.BitmapCacheUtils;
import com.itcast.smartbj.utils.MyConstants;
import com.itcast.smartbj.utils.SpTools;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PhotosBaseNewsCenterPage extends BaseNewsCenterPage{
	@ViewInject(R.id.lv_newscenter_photos)
	private ListView lv_photos;
	@ViewInject(R.id.gv_newscenter_photos)
	private GridView gv_photos;
	private MyAdapter adapter;
	private List<PhotosNews> photosNews = new ArrayList<PhotosData.PhotosData_Data.PhotosNews>();
	private BitmapUtils bitmapUtils;
	private boolean isShowList = true;
	private BitmapCacheUtils bitmapCacheUtils;

	public PhotosBaseNewsCenterPage(MainActivity mainActivity) {
		super(mainActivity);
		bitmapUtils = new BitmapUtils(mainActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		bitmapCacheUtils = new BitmapCacheUtils(mainActivity);
	}

	@Override
	public View initView() {
		
		View photos_root = View.inflate(mainActivity, R.layout.newscenter_photos, null);
		ViewUtils.inject(this, photos_root);
		return photos_root;
		
	}
	
	
	public void switchListViewOrGridView(ImageButton ib_listOrGrid) {
		if (isShowList) {
			//换成显示gridview
			gv_photos.setVisibility(View.VISIBLE);
			lv_photos.setVisibility(View.GONE);
			//isShowList = false;
			ib_listOrGrid.setImageResource(R.drawable.icon_pic_list_type);
		}else {
			gv_photos.setVisibility(View.GONE);
			lv_photos.setVisibility(View.VISIBLE);
			//isShowList = true;
			ib_listOrGrid.setImageResource(R.drawable.icon_pic_grid_type);
		}
		isShowList = !isShowList;
	}
	
	@Override
	public void initData() {
		//初始化适配器 并作用于listview和gridview
		if (adapter == null) {
			adapter = new MyAdapter();
			lv_photos.setAdapter(adapter);
			gv_photos.setAdapter(adapter);
		}
		
		if (isShowList) {
			lv_photos.setVisibility(View.VISIBLE);
			gv_photos.setVisibility(View.GONE);
		}else {
			gv_photos.setVisibility(View.VISIBLE);
			lv_photos.setVisibility(View.GONE);
		}
		
		
		//本地取数据
		String photosJsonData = SpTools.getString(mainActivity, MyConstants.PHOTOURL, null);
		if (!TextUtils.isEmpty(photosJsonData )) {
			//有数据就解析数据    处理数据
			PhotosData photosData = parsePhotosJson(photosJsonData );
			processPhotosData(photosData );
		}
		//网络取数据
		getDataFromNet();
	}

	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, MyConstants.PHOTOURL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//获取json数据
				String jsonData = responseInfo.result;
				
				//缓存
				SpTools.setString(mainActivity, MyConstants.PHOTOURL, jsonData);
				
				//解析数据--->domain
				PhotosData photosData = parsePhotosJson(jsonData);
				
				//处理数据
				processPhotosData(photosData );
				
			}		

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	protected void processPhotosData(PhotosData photosData) {
		//获取到了组图的直接数据
		photosNews = photosData.data.news;
		//通知界面更新
		adapter.notifyDataSetChanged();	
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photosNews.size();
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mainActivity, R.layout.photoslist_item, null);
				holder = new ViewHolder();
				holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_photos_list_item_pic);
				holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_photos_list_item_desc);
				convertView.setTag(holder);
			}else {
				 holder = (ViewHolder) convertView.getTag();
			}
			PhotosNews pn = photosNews.get(position);
			//标题
			holder.tv_desc.setText(pn.title);
			//图片
			//bitmapUtils.display(holder.iv_pic, pn.listimage);
			bitmapCacheUtils.display(holder.iv_pic, pn.listimage);
			return convertView;
		}
		
	}
	
	private class ViewHolder{
		ImageView	iv_pic;
		TextView	tv_desc;
	}

	protected PhotosData parsePhotosJson(String jsonData) {
		Gson gson = new Gson();
		PhotosData photosData = gson.fromJson(jsonData, PhotosData.class);
		return photosData;
	}

}
