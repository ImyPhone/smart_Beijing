package com.itcast.smartbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpTools {
	
	//��Ż�������
	public static void setString(Context context, String key, String value){
		SharedPreferences sp = context.getSharedPreferences(MyConstants.MYCONFIG, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	
	//ȡ��������
	public static String getString(Context context,String key,String defValue){
		SharedPreferences sp = context.getSharedPreferences(MyConstants.MYCONFIG, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}
}
