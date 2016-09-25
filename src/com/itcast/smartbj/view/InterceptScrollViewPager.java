package com.itcast.smartbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//父类不拦截的viewPager  自己处理touch事件
public class InterceptScrollViewPager extends ViewPager {

	private float downX;
	private float downY;

	public InterceptScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public InterceptScrollViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//true 请求父控件不拦截我的touch事件,事件完全由自己处理    false默认父类先拦截事件
		//getParent().requestDisallowInterceptTouchEvent(true);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			downX = ev.getX();
			downY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = ev.getX();
			float moveY = ev.getY();
			float dx = moveX - downX;
			float dy = moveY - downY;
			if (Math.abs(dx)>Math.abs(dy)) {
				if (getCurrentItem() == 0 && dx > 0 ) {
					//如果在我的Viewpager第一个页面，并且从左往右滑动  让父控件拦截我，自己不做处理
					getParent().requestDisallowInterceptTouchEvent(false);
					
				}else if (getCurrentItem() == getAdapter().getCount()-1 && dx<0) {
					//如果在我的Viewpager最后一个页面，并且从右往左滑动 让父控件拦截  响应外部touch事件
					getParent().requestDisallowInterceptTouchEvent(false);
					
				}else {
					//否则都不让父类拦截,自己处理
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			}else {
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			

			
			break;
		default:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}
}
