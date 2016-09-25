package com.itcast.smartbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//���಻���ص�viewPager  �Լ�����touch�¼�
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
		//true ���󸸿ؼ��������ҵ�touch�¼�,�¼���ȫ���Լ�����    falseĬ�ϸ����������¼�
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
					//������ҵ�Viewpager��һ��ҳ�棬���Ҵ������һ���  �ø��ؼ������ң��Լ���������
					getParent().requestDisallowInterceptTouchEvent(false);
					
				}else if (getCurrentItem() == getAdapter().getCount()-1 && dx<0) {
					//������ҵ�Viewpager���һ��ҳ�棬���Ҵ������󻬶� �ø��ؼ�����  ��Ӧ�ⲿtouch�¼�
					getParent().requestDisallowInterceptTouchEvent(false);
					
				}else {
					//���򶼲��ø�������,�Լ�����
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
