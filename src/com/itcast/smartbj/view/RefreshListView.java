package com.itcast.smartbj.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itcast.smartbj.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RefreshListView extends ListView {

	private LinearLayout head;
	private View foot;
	private LinearLayout ll_refresh_head_root;
	private int ll_refresh_head_root_Height;
	private int ll_refresh_foot_Height;
	private float downY = -1;
	private View lunbotu;
	private int listViewOnScreanY; // listview����Ļ��Y������λ��
	private final int PULL_DOWN = 1; // ����ˢ��״̬
	private final int RELEASE_STATE = 2; // �ɿ�ˢ��
	private final int REFRESHING = 3; // ����ˢ��
	private int currentState = PULL_DOWN; // ��ǰ״̬
	private TextView tv_state;
	private TextView tv_time;
	private ImageView iv_arrow;
	private ProgressBar pb_loading;
	private RotateAnimation up_ra;
	private RotateAnimation down_ra;
	private OnRefreshDataListener listener;
	private boolean isEnablePullRefresh;//����ˢ���Ƿ����
	private boolean isLoadingMore;

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
		initAnimation();
		initEvent();
	}

	private void initEvent() {
		setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ��������״̬��3�֣���ֻҪlistview��ʾ���һ�� ->���ظ���
				if (getLastVisiblePosition() == getAdapter().getCount()-1 && !isLoadingMore) {
					foot.setPadding(0, 0, 0, 0);
					//���ظ��������
					setSelection(getAdapter().getCount()); 
					isLoadingMore = true;
					if (listener != null) {
						listener.loadingMore(); //ʵ�ָýӿڵ����ȥ������ݵļ���
					}
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	public RefreshListView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// ��Ҫ���ǵĹ��� ���ε������touch�¼�
		// ��ʾ��һ������ �������϶�

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isEnablePullRefresh) {
				break;
			}
			//�����Ƿ�������ˢ��״̬
			if (currentState == REFRESHING) {
				break;
			}

			if (!isLunboFullShow()) {
				// �ֲ�ͼû����ȫ��ʾ ����switch���
				break;
			}

			if (downY == -1) { // ��ֹ���µ�ʱ��û�л�ȡ����
				downY = ev.getY();
			}

			float moveY = ev.getY();

			float dy = moveY - downY;
			// getFirstVisiblePosition() ��Ļ��ʾ�ĵ�һ��item����Ӧ��position
			if (dy > 0 && getFirstVisiblePosition() == 0) {
				// ��ǰpadding top�Ĳ���ֵ
				float scrollYDis = -ll_refresh_head_root_Height + dy;

				if (scrollYDis < 0 && currentState != PULL_DOWN) {
					// System.out.println("����ˢ��");
					currentState = PULL_DOWN;
					refreshState();
				} else if (scrollYDis >= 0 && currentState != RELEASE_STATE) {
					// System.out.println("�ɿ�ˢ��");
					currentState = RELEASE_STATE;
					refreshState();
				}

				ll_refresh_head_root.setPadding(0, (int) scrollYDis, 0, 0);
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			downY = -1;
			if (currentState == PULL_DOWN) {
				//����ˢ��״̬  ->�ɿ��ظ�ԭ״
				ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
			}else if (currentState == RELEASE_STATE) {
				//�ɿ�ˢ��״̬ ->��ȫ��ʾˢ��ͷ ->ȥ����ˢ������ ->ˢ������
				ll_refresh_head_root.setPadding(0, 0, 0, 0);
				currentState = REFRESHING;
				refreshState();
				
				if (listener != null) {
					listener.refreshData();
				}
			}
			

			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	//ʲôʱ��Ҫˢ���Ҳ�֪������ôˢ��TPINewsNewsCenterPager��֪��
	//refreshData()����ʲôʱ��ִ���Ҿ���   
	public void setOnRefreshDataListener(OnRefreshDataListener listener) {
		this.listener = listener;
	}
	
	public interface OnRefreshDataListener{
		void refreshData();
		void loadingMore();
	}
	
	public void refreshDataFinish() {
		if (isLoadingMore) {
			//���ظ���������ɺ�
			isLoadingMore = false;
			foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
		}else {
			//����ˢ��������ɺ�
			tv_state.setText("����ˢ��");
			iv_arrow.startAnimation(down_ra);	
			pb_loading.setVisibility(View.INVISIBLE);
			tv_time.setText(getCurrentFormatDate());
			ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);//����ͷ����
			currentState = PULL_DOWN;
		}	
	}
	
	private String getCurrentFormatDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	private void initAnimation() {
		up_ra = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		up_ra.setDuration(500);
		up_ra.setFillAfter(true);
		
		down_ra = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		down_ra .setDuration(500);
		down_ra .setFillAfter(true);
	}
	
	private void refreshState() {
		switch (currentState) {
		case PULL_DOWN:// ����ˢ��
			tv_state.setText("����ˢ��");
			iv_arrow.startAnimation(down_ra);			
			break;
		case RELEASE_STATE:// �ɿ�ˢ��
			tv_state.setText("�ɿ�ˢ��");
			iv_arrow.startAnimation(up_ra);
			break;
		case REFRESHING:
			iv_arrow.clearAnimation();
			iv_arrow.setVisibility(View.INVISIBLE);
			pb_loading.setVisibility(View.VISIBLE);
			tv_state.setText("����������...");
			break;

		default:
			break;
		}

	}

	// �ж��ֲ�ͼ�Ƿ���ȫ��ʾ
	private boolean isLunboFullShow() {

		int[] location = new int[2];

		if (listViewOnScreanY == 0) {
			this.getLocationOnScreen(location);
			listViewOnScreanY = location[1];
		}

		lunbotu.getLocationOnScreen(location);
		if (location[1] < listViewOnScreanY) {

			return false;
		}
		return true;

	}

	private void initView() {
		initHead();
		initFoot();
	}

	private void initFoot() {
		foot = View.inflate(getContext(), R.layout.listview_refresh_foot,
				null);
		foot.measure(0, 0);

		// listviewβ������ĸ߶�
		ll_refresh_foot_Height = foot.getMeasuredHeight();
		foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
		this.addFooterView(foot);
	}

	private void initHead() {
		head = (LinearLayout) View.inflate(getContext(),
				R.layout.listview_head_container, null);
		// listviewˢ��ͷ�ĸ�����
		ll_refresh_head_root = (LinearLayout) head
				.findViewById(R.id.ll_listview_head_root);

		tv_state = (TextView) head
				.findViewById(R.id.tv_listview_head_state_dec);

		tv_time = (TextView) head
				.findViewById(R.id.tv_listview_head_refresh_time);
		
		iv_arrow = (ImageView) head.findViewById(R.id.iv_listview_head_arrow);
		
		pb_loading = (ProgressBar) head
				.findViewById(R.id.pb_listview_head_loading);

		// ����ˢ��ͷ�ĸ����� �ֲ�ͼ��Ҫ��ʾ
		ll_refresh_head_root.measure(0, 0);
		ll_refresh_head_root_Height = ll_refresh_head_root.getMeasuredHeight();
		ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
		
		this.addHeaderView(head);
	}
	
	//�û��Լ�ѡ���Ƿ���������ˢ��ͷ�Ĺ���
	public void setIsRefreshHead(boolean isPullrefresh) {
		isEnablePullRefresh = isPullrefresh;
	}

	@Override
	public void addHeaderView(View v) {
		//�ж� ���ʹ��������ˢ�£���ͷ���ּӵ�����ˢ�µ�������������ص�ԭ����listview��
		if (isEnablePullRefresh) {
			//��������ˢ��
			lunbotu = v;
			head.addView(v);
		}else {
			//ʹ��ԭ����listview
			super.addHeaderView(v);
		}		
	}
	
	public void addLunboView(View view) {
		lunbotu = view;
		head.addView(view);// LinearLayout��ӵĵڶ���view �ֲ�ͼ
	}

}
