package com.itcast.smartbj.basepage;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.gson.Gson;
import com.itcast.smartbj.activity.MainActivity;
import com.itcast.smartbj.domain.NewsCenterData;
import com.itcast.smartbj.newscenterpage.BaseNewsCenterPage;
import com.itcast.smartbj.newscenterpage.InteractBaseNewsCenterPage;
import com.itcast.smartbj.newscenterpage.NewsBaseNewsCenterPage;
import com.itcast.smartbj.newscenterpage.PhotosBaseNewsCenterPage;
import com.itcast.smartbj.newscenterpage.TopicBaseNewsCenterPage;
import com.itcast.smartbj.utils.MyConstants;
import com.itcast.smartbj.utils.SpTools;
import com.itcast.smartbj.view.LeftMenuFragment.OnSwitchPageListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;



public class NewsCenterBaseTagPager extends BaseTagPage {
	//新闻中心要显示的页面
	private List<BaseNewsCenterPage> newsCenterPages = new ArrayList<BaseNewsCenterPage>();
	private NewsCenterData newsCenterData;
	private Gson gson;

	public NewsCenterBaseTagPager(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}
	
	
	//手动调用此方法
	@Override
	public void initData() {
		//1.获取本地数据
		String cacheData = SpTools.getString(mainActivity, MyConstants.NEWSCENTERURL, "");
		if (!(TextUtils.isEmpty(cacheData))) {
			parseData(cacheData);
		}
		
		//2.获取网络数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, MyConstants.NEWSCENTERURL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				//保存到本地一份
				SpTools.setString(mainActivity, MyConstants.NEWSCENTERURL, result);
				//3.解析数据
				parseData(result);
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("请求失败");
				
			}
		});
		
		/*try {
			URL url = new URL("http://10.0.2.2:8080/zhbj/categories.json");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			int code = connection.getResponseCode();
			System.out.println("code="+code);
			if (code == 200) {
				InputStream is = connection.getInputStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len=is.read(buffer))!= -1){
					os.write(buffer,0,len);
				}
				String result = new String(os.toByteArray());
				System.out.println(result);
				tv.setText(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private void parseData(String jsonData) {
		if (gson == null) {
			gson = new Gson();
		}
		newsCenterData = gson.fromJson(jsonData, NewsCenterData.class);
		
		
		//4.数据的处理
		
		//在这里给左侧菜单设置数据
		mainActivity.getLeftMenuFragment().setMenuData(newsCenterData.data);
		mainActivity.getLeftMenuFragment().setOnSwitchPageListener(new OnSwitchPageListener() {
			
			@Override
			public void switchPage(int selectPosition) { //点击了左菜单哪个selectPosition，只有左菜单自己知道
														//点击后切面的切换只能通过NewsCenterBaseTagPager的switchPage方法
				NewsCenterBaseTagPager.this.switchPage(selectPosition);
				//System.out.println("通过LeftMenuFragment的接口回调实现新闻页面的切换");
			}
		});
		
		//读取的数据封装到界面容器中，通过左侧菜单点击显示不同的界面
		//根据服务器的数据 创建四个页面（按顺序）		
		for (NewsCenterData.NewsData newsData : newsCenterData.data) {
			//遍历4个新闻中心页面
			BaseNewsCenterPage newsPage = null;
			switch (newsData.type) {
			case 1: //新闻页面
				newsPage = new NewsBaseNewsCenterPage(mainActivity,newsCenterData.data.get(0).children);
				break;
			case 10: //专题页面
				newsPage = new TopicBaseNewsCenterPage(mainActivity);
				break;
			case 2: //组图页面
				newsPage = new PhotosBaseNewsCenterPage(mainActivity);
				break;
			case 3: //互动页面
				newsPage = new InteractBaseNewsCenterPage(mainActivity);
				break;

			default:
				break;
			}
			//添加新闻中心的页面到容器中
			newsCenterPages.add(newsPage);
		}
		
		//控制4个页面的显示,默认选择第一个新闻页面
		switchPage(0);
	}
	
	//根据传递过来的位置 动态显示不同新闻中心的页面
	public void switchPage(int positon) {

		//   title的position按照解析数据的顺序来，而4个页面加载到容器也是按解析数据顺序，
		//      这就保证了页面和标题对应
		tv_title.setText(newsCenterData.data.get(positon).title);
		BaseNewsCenterPage baseNewsCenterPage = newsCenterPages.get(positon);
		
		//移除原内容
		fl_content.removeAllViews();
		
		//初始化数据
		baseNewsCenterPage.initData();
		
		//如果是组图的话 显示listview和gridview切换按钮
		if (baseNewsCenterPage instanceof PhotosBaseNewsCenterPage) {
			ib_listOrGrid.setVisibility(View.VISIBLE);
			
			ib_listOrGrid.setTag(baseNewsCenterPage);
			//点击   listview和gridview切换
			
			ib_listOrGrid.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {	//  ib_listOrGrid<==>PhotosBaseNewsCenterPage 互调
					
					((PhotosBaseNewsCenterPage) ib_listOrGrid.getTag()).switchListViewOrGridView(ib_listOrGrid);
					
				}
			});
			
		}else {
			ib_listOrGrid.setVisibility(View.GONE);			
		}
		
		//替换白纸
		fl_content.addView(baseNewsCenterPage.getRoot());
	}
}
