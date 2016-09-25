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
	private int listViewOnScreanY; // listview在屏幕中Y轴坐标位置
	private final int PULL_DOWN = 1; // 下拉刷新状态
	private final int RELEASE_STATE = 2; // 松开刷新
	private final int REFRESHING = 3; // 正在刷新
	private int currentState = PULL_DOWN; // 当前状态
	private TextView tv_state;
	private TextView tv_time;
	private ImageView iv_arrow;
	private ProgressBar pb_loading;
	private RotateAnimation up_ra;
	private RotateAnimation down_ra;
	private OnRefreshDataListener listener;
	private boolean isEnablePullRefresh;//下拉刷新是否可用
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
				// 不管哪种状态（3种），只要listview显示最后一条 ->加载更多
				if (getLastVisiblePosition() == getAdapter().getCount()-1 && !isLoadingMore) {
					foot.setPadding(0, 0, 0, 0);
					//加载更多的数据
					setSelection(getAdapter().getCount()); 
					isLoadingMore = true;
					if (listener != null) {
						listener.loadingMore(); //实现该接口的组件去完成数据的加载
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
		// 需要我们的功能 屏蔽掉父类的touch事件
		// 显示第一条数据 的下拉拖动

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isEnablePullRefresh) {
				break;
			}
			//现在是否处于正在刷新状态
			if (currentState == REFRESHING) {
				break;
			}

			if (!isLunboFullShow()) {
				// 轮播图没有完全显示 跳出switch语句
				break;
			}

			if (downY == -1) { // 防止按下的时候没有获取坐标
				downY = ev.getY();
			}

			float moveY = ev.getY();

			float dy = moveY - downY;
			// getFirstVisiblePosition() 屏幕显示的第一个item所对应的position
			if (dy > 0 && getFirstVisiblePosition() == 0) {
				// 当前padding top的参数值
				float scrollYDis = -ll_refresh_head_root_Height + dy;

				if (scrollYDis < 0 && currentState != PULL_DOWN) {
					// System.out.println("下拉刷新");
					currentState = PULL_DOWN;
					refreshState();
				} else if (scrollYDis >= 0 && currentState != RELEASE_STATE) {
					// System.out.println("松开刷新");
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
				//下拉刷新状态  ->松开回复原状
				ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
			}else if (currentState == RELEASE_STATE) {
				//松开刷新状态 ->完全显示刷新头 ->去真正刷新数据 ->刷好了再
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
	
	//什么时候要刷新我才知道，怎么刷新TPINewsNewsCenterPager才知道
	//refreshData()方法什么时候执行我决定   
	public void setOnRefreshDataListener(OnRefreshDataListener listener) {
		this.listener = listener;
	}
	
	public interface OnRefreshDataListener{
		void refreshData();
		void loadingMore();
	}
	
	public void refreshDataFinish() {
		if (isLoadingMore) {
			//加载更多数据完成后
			isLoadingMore = false;
			foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
		}else {
			//下拉刷新数据完成后
			tv_state.setText("下拉刷新");
			iv_arrow.startAnimation(down_ra);	
			pb_loading.setVisibility(View.INVISIBLE);
			tv_time.setText(getCurrentFormatDate());
			ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);//隐藏头布局
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
		case PULL_DOWN:// 下拉刷新
			tv_state.setText("下拉刷新");
			iv_arrow.startAnimation(down_ra);			
			break;
		case RELEASE_STATE:// 松开刷新
			tv_state.setText("松开刷新");
			iv_arrow.startAnimation(up_ra);
			break;
		case REFRESHING:
			iv_arrow.clearAnimation();
			iv_arrow.setVisibility(View.INVISIBLE);
			pb_loading.setVisibility(View.VISIBLE);
			tv_state.setText("玩命加载中...");
			break;

		default:
			break;
		}

	}

	// 判断轮播图是否完全显示
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

		// listview尾部组件的高度
		ll_refresh_foot_Height = foot.getMeasuredHeight();
		foot.setPadding(0, -ll_refresh_foot_Height, 0, 0);
		this.addFooterView(foot);
	}

	private void initHead() {
		head = (LinearLayout) View.inflate(getContext(),
				R.layout.listview_head_container, null);
		// listview刷新头的根布局
		ll_refresh_head_root = (LinearLayout) head
				.findViewById(R.id.ll_listview_head_root);

		tv_state = (TextView) head
				.findViewById(R.id.tv_listview_head_state_dec);

		tv_time = (TextView) head
				.findViewById(R.id.tv_listview_head_refresh_time);
		
		iv_arrow = (ImageView) head.findViewById(R.id.iv_listview_head_arrow);
		
		pb_loading = (ProgressBar) head
				.findViewById(R.id.pb_listview_head_loading);

		// 隐藏刷新头的根布局 轮播图还要显示
		ll_refresh_head_root.measure(0, 0);
		ll_refresh_head_root_Height = ll_refresh_head_root.getMeasuredHeight();
		ll_refresh_head_root.setPadding(0, -ll_refresh_head_root_Height, 0, 0);
		
		this.addHeaderView(head);
	}
	
	//用户自己选择，是否启用下拉刷新头的功能
	public void setIsRefreshHead(boolean isPullrefresh) {
		isEnablePullRefresh = isPullrefresh;
	}

	@Override
	public void addHeaderView(View v) {
		//判断 如果使用了下拉刷新，把头布局加到下拉刷新的容器，否则加载到原生的listview中
		if (isEnablePullRefresh) {
			//启用下拉刷新
			lunbotu = v;
			head.addView(v);
		}else {
			//使用原生的listview
			super.addHeaderView(v);
		}		
	}
	
	public void addLunboView(View view) {
		lunbotu = view;
		head.addView(view);// LinearLayout添加的第二个view 轮播图
	}

}
