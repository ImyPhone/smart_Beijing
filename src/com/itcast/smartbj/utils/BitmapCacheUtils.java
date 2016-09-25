package com.itcast.smartbj.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.itcast.smartbj.activity.MainActivity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class BitmapCacheUtils {
	
	//��̬��ȡjvm�ڴ�
	private int maxSize = (int) (Runtime.getRuntime().freeMemory()/2);
	
	//ͼƬ�Ļ�������
	private LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>((int) maxSize){
		//����ͼƬռ�ö��
		protected int sizeOf(String key, Bitmap value) {
			//return value.getByteCount();
			return value.getRowBytes() * value.getHeight();
		};		
	};
	
	//��������һ�η���URL����Ϣ
	private Map<ImageView, String> urlImageViewDatas = new HashMap<ImageView, String>();
	private File cacheDir;
	private MainActivity mainActivity;
	private ExecutorService threadPool;	
	public BitmapCacheUtils(MainActivity mainActivity){
		this.mainActivity = mainActivity;
		cacheDir = mainActivity.getCacheDir();//��ȡ��ǰAPP�Ļ���Ŀ¼
		threadPool = Executors.newFixedThreadPool(6);
	}
	
	
	public Bitmap getCacheFile(String ivUrl) {
		
		//ivUrlתMD5��ֵ��ΪͼƬ������
		
		File file = new File(cacheDir, Md5Utils.md5(ivUrl));
		if (file != null && file.exists()) {
			//�ļ�����
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			
			//�����ڴ���дһ��
			memCache.put(ivUrl, bitmap);
			
			return bitmap;
		}else {
			//������
			return null;
		}
	}
	
	//����bitmap��cacheĿ¼���ļ���
	public void saveBitmapToCacheFile(Bitmap bitmap, String ivUrl) {
		File file = new File(cacheDir, Md5Utils.md5(ivUrl));
		try {
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	private void getBitmapFromNet(ImageView iv, String ivUrl) {
		
		//new Thread(new DownLoadUrl(iv, ivUrl)).start();
		threadPool.submit(new DownLoadUrl(iv, ivUrl));
	}
	
	private class DownLoadUrl implements Runnable{
		private String ivUrl;
		private ImageView iv;
		public DownLoadUrl(ImageView iv, String ivUrl){
			this.ivUrl = ivUrl;
			this.iv = iv;			
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(ivUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream inputStream = conn.getInputStream();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					//1.���ڴ������
					memCache.put(ivUrl, bitmap);
					
					//2.�������ļ������
					saveBitmapToCacheFile(bitmap, ivUrl);
					
					//3.��ʾͼƬ
					mainActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//�ж�url�ǲ������µ�
							if (ivUrl.equals(urlImageViewDatas.get(iv))) {
								iv.setImageBitmap(bitmap);
							}							
							System.out.println("�������ȡ����");						
						}
					});					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
	}
	
	
	public void display(ImageView iv, String ivUrl) {
		
		//1.�ڴ��ȡ
		Bitmap bitmap = memCache.get(ivUrl);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			System.out.println("���ڴ��ȡ����");
			return;
		}
		
		//2.���ػ�ȡ
		bitmap = getCacheFile(ivUrl);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			System.out.println("���ļ���ȡ����");
			return;
		}
		
		//3.�����ȡ
		urlImageViewDatas.put(iv, ivUrl);//�������һ�η��ʵ�URL  �ؼ���һ��ʱ ֵ�Ḳ��
		getBitmapFromNet(iv, ivUrl);
	}

	
}
