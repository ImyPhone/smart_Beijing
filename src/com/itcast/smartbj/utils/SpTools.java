package com.itcast.smartbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpTools {
	
	//存放缓存数据
	public static void setString(Context context, String key, String value){
		SharedPreferences sp = context.getSharedPreferences(MyConstants.MYCONFIG, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	
	//取缓存数据
	public static String getString(Context context,String key,String defValue){
		SharedPreferences sp = context.getSharedPreferences(MyConstants.MYCONFIG, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}
}
