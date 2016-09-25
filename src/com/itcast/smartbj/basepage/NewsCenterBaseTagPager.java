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
	//��������Ҫ��ʾ��ҳ��
	private List<BaseNewsCenterPage> newsCenterPages = new ArrayList<BaseNewsCenterPage>();
	private NewsCenterData newsCenterData;
	private Gson gson;

	public NewsCenterBaseTagPager(MainActivity mainActivity) {
		super(mainActivity);
		// TODO Auto-generated constructor stub
	}
	
	
	//�ֶ����ô˷���
	@Override
	public void initData() {
		//1.��ȡ��������
		String cacheData = SpTools.getString(mainActivity, MyConstants.NEWSCENTERURL, "");
		if (!(TextUtils.isEmpty(cacheData))) {
			parseData(cacheData);
		}
		
		//2.��ȡ��������
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, MyConstants.NEWSCENTERURL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				//���浽����һ��
				SpTools.setString(mainActivity, MyConstants.NEWSCENTERURL, result);
				//3.��������
				parseData(result);
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("����ʧ��");
				
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
		
		
		//4.���ݵĴ���
		
		//����������˵���������
		mainActivity.getLeftMenuFragment().setMenuData(newsCenterData.data);
		mainActivity.getLeftMenuFragment().setOnSwitchPageListener(new OnSwitchPageListener() {
			
			@Override
			public void switchPage(int selectPosition) { //�������˵��ĸ�selectPosition��ֻ����˵��Լ�֪��
														//�����������л�ֻ��ͨ��NewsCenterBaseTagPager��switchPage����
				NewsCenterBaseTagPager.this.switchPage(selectPosition);
				//System.out.println("ͨ��LeftMenuFragment�Ľӿڻص�ʵ������ҳ����л�");
			}
		});
		
		//��ȡ�����ݷ�װ�����������У�ͨ�����˵������ʾ��ͬ�Ľ���
		//���ݷ����������� �����ĸ�ҳ�棨��˳��		
		for (NewsCenterData.NewsData newsData : newsCenterData.data) {
			//����4����������ҳ��
			BaseNewsCenterPage newsPage = null;
			switch (newsData.type) {
			case 1: //����ҳ��
				newsPage = new NewsBaseNewsCenterPage(mainActivity,newsCenterData.data.get(0).children);
				break;
			case 10: //ר��ҳ��
				newsPage = new TopicBaseNewsCenterPage(mainActivity);
				break;
			case 2: //��ͼҳ��
				newsPage = new PhotosBaseNewsCenterPage(mainActivity);
				break;
			case 3: //����ҳ��
				newsPage = new InteractBaseNewsCenterPage(mainActivity);
				break;

			default:
				break;
			}
			//����������ĵ�ҳ�浽������
			newsCenterPages.add(newsPage);
		}
		
		//����4��ҳ�����ʾ,Ĭ��ѡ���һ������ҳ��
		switchPage(0);
	}
	
	//���ݴ��ݹ�����λ�� ��̬��ʾ��ͬ�������ĵ�ҳ��
	public void switchPage(int positon) {

		//   title��position���ս������ݵ�˳��������4��ҳ����ص�����Ҳ�ǰ���������˳��
		//      ��ͱ�֤��ҳ��ͱ����Ӧ
		tv_title.setText(newsCenterData.data.get(positon).title);
		BaseNewsCenterPage baseNewsCenterPage = newsCenterPages.get(positon);
		
		//�Ƴ�ԭ����
		fl_content.removeAllViews();
		
		//��ʼ������
		baseNewsCenterPage.initData();
		
		//�������ͼ�Ļ� ��ʾlistview��gridview�л���ť
		if (baseNewsCenterPage instanceof PhotosBaseNewsCenterPage) {
			ib_listOrGrid.setVisibility(View.VISIBLE);
			
			ib_listOrGrid.setTag(baseNewsCenterPage);
			//���   listview��gridview�л�
			
			ib_listOrGrid.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {	//  ib_listOrGrid<==>PhotosBaseNewsCenterPage ����
					
					((PhotosBaseNewsCenterPage) ib_listOrGrid.getTag()).switchListViewOrGridView(ib_listOrGrid);
					
				}
			});
			
		}else {
			ib_listOrGrid.setVisibility(View.GONE);			
		}
		
		//�滻��ֽ
		fl_content.addView(baseNewsCenterPage.getRoot());
	}
}
