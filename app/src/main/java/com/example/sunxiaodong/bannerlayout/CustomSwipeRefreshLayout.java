package com.example.sunxiaodong.bannerlayout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sunxiaodong on 16/4/12.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final int DISTANCE = 24;//y方向上启动距离

    private float mInitialDownX;
    private float mInitialDownY;
    private float mLastMotionX;
    private float mLastMotionY;

    private boolean mIsIntercept;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownX = ev.getX();
                mInitialDownY = ev.getY();
                mIsIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                float movedY = mLastMotionY - mInitialDownY;
                if (movedY > DISTANCE) {
                    float xDiff = Math.abs(mLastMotionX - mInitialDownX);
                    if (movedY >= xDiff) {
                        mIsIntercept = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsIntercept = false;
                break;
        }
        return ret && mIsIntercept;
    }

}
