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
	
	//动态获取jvm内存
	private int maxSize = (int) (Runtime.getRuntime().freeMemory()/2);
	
	//图片的缓存容器
	private LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>((int) maxSize){
		//计算图片占用多大
		protected int sizeOf(String key, Bitmap value) {
			//return value.getByteCount();
			return value.getRowBytes() * value.getHeight();
		};		
	};
	
	//保留最有一次访问URL的信息
	private Map<ImageView, String> urlImageViewDatas = new HashMap<ImageView, String>();
	private File cacheDir;
	private MainActivity mainActivity;
	private ExecutorService threadPool;	
	public BitmapCacheUtils(MainActivity mainActivity){
		this.mainActivity = mainActivity;
		cacheDir = mainActivity.getCacheDir();//获取当前APP的缓存目录
		threadPool = Executors.newFixedThreadPool(6);
	}
	
	
	public Bitmap getCacheFile(String ivUrl) {
		
		//ivUrl转MD5的值作为图片的名字
		
		File file = new File(cacheDir, Md5Utils.md5(ivUrl));
		if (file != null && file.exists()) {
			//文件存在
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			
			//再往内存中写一份
			memCache.put(ivUrl, bitmap);
			
			return bitmap;
		}else {
			//不存在
			return null;
		}
	}
	
	//保存bitmap到cache目录的文件中
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
					//1.往内存中添加
					memCache.put(ivUrl, bitmap);
					
					//2.往本地文件中添加
					saveBitmapToCacheFile(bitmap, ivUrl);
					
					//3.显示图片
					mainActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//判断url是不是最新的
							if (ivUrl.equals(urlImageViewDatas.get(iv))) {
								iv.setImageBitmap(bitmap);
							}							
							System.out.println("从网络获取数据");						
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
		
		//1.内存获取
		Bitmap bitmap = memCache.get(ivUrl);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			System.out.println("从内存获取数据");
			return;
		}
		
		//2.本地获取
		bitmap = getCacheFile(ivUrl);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			System.out.println("从文件获取数据");
			return;
		}
		
		//3.网络获取
		urlImageViewDatas.put(iv, ivUrl);//保留最后一次访问的URL  关键字一样时 值会覆盖
		getBitmapFromNet(iv, ivUrl);
	}

	
}
