package com.itcast.smartbj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itcast.smartbj.R;
import com.itcast.smartbj.utils.SharedAppUtils;

public class NewsDetailAcitivity extends Activity {
	
	private ImageButton ib_back;
	private ImageButton ib_setTextSize;
	private ImageButton ib_share;
	private WebView wv_news;
	private ProgressBar pb_loadingnews;
	private WebSettings wv_setting; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	private void initView() {
		setContentView(R.layout.newscenter_newsdetail);
		findViewById(R.id.ib_base_content_menu).setVisibility(View.GONE);
		findViewById(R.id.tv_base_content_title).setVisibility(View.GONE);
		
		ib_back = (ImageButton) findViewById(R.id.ib_base_content_back);
		ib_back.setVisibility(View.VISIBLE);
		
		ib_setTextSize = (ImageButton) findViewById(R.id.ib_base_content_textsize);
		ib_setTextSize.setVisibility(View.VISIBLE);
		
		ib_share = (ImageButton) findViewById(R.id.ib_base_content_share);
		ib_share.setVisibility(View.VISIBLE);
		
		wv_news = (WebView) findViewById(R.id.wv_newscenter_newsdetail);
		
		//����WebSettings���� ����Webview����ʾ����
		wv_setting = wv_news.getSettings();
		wv_setting.setBuiltInZoomControls(true);//���÷Ŵ���С
		wv_setting.setJavaScriptEnabled(true);//����ȥ����javsscript�ű�
		wv_setting.setUseWideViewPort(true);//˫���Ŵ����С

		pb_loadingnews = (ProgressBar) findViewById(R.id.pb_newscenter_newsdtail_loading);
	}

	private void initData() {
		String newsurl = getIntent().getStringExtra("newsurl");
		if (TextUtils.isEmpty(newsurl)) {
			Toast.makeText(getApplicationContext(), "���Ӵ���", 1).show();
		}else {
			wv_news.loadUrl(newsurl);
		}
		
	}

	private void initEvent() {
		OnClickListener listener = new OnClickListener() {
			int textSizeIndex = 2;// 0. ����� 1,���  2 ����  3 С��  4 ��С��
			private AlertDialog	dialog;
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ib_base_content_back:
					finish();
					break;
					
				case R.id.ib_base_content_textsize:
					showChangeTextSizeDialog();
					break;
					
				case R.id.ib_base_content_share:
					SharedAppUtils.showShare(getApplicationContext());
					break;

				default:
					break;
				}
				
			}

			private void showChangeTextSizeDialog() {
				AlertDialog.Builder builder = new Builder(NewsDetailAcitivity.this);
				builder.setTitle("ѡ�������С");
				String[] textSize = {"�����","���","����","С��","��С��"}; 
				builder.setSingleChoiceItems(textSize, textSizeIndex, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						textSizeIndex = which;
						setTextSize();
					}

				});
				dialog = builder.create();
				dialog.show();
			}
			
			private void setTextSize() {
				switch (textSizeIndex) {
				case 0://�����
					wv_setting.setTextSize(TextSize.LARGEST);
					break;
				case 1: //���
					wv_setting.setTextSize(TextSize.LARGER);
					break;
				case 2: //����
					wv_setting.setTextSize(TextSize.NORMAL);
					break;
				case 3: //С��
					wv_setting.setTextSize(TextSize.SMALLER);
					break;
				case 4: //��С��
					wv_setting.setTextSize(TextSize.SMALLEST);
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		};
		ib_back.setOnClickListener(listener);
		ib_setTextSize.setOnClickListener(listener);
		ib_share.setOnClickListener(listener);
		
		wv_news.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				pb_loadingnews.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
			
		});
		
	}
}
