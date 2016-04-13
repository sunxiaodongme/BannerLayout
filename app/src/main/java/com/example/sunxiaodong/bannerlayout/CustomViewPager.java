package com.example.sunxiaodong.bannerlayout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sunxiaodong on 16/4/12.
 */
public class CustomViewPager extends ViewPager {

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private float mLastMotionY;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);
        final int action = ev.getAction();
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                float movedY = mLastMotionY - mInitialMotionY;
                if (movedY > 0) {
                    float xDiff = Math.abs(mLastMotionX - mInitialMotionX);
                    if (movedY < xDiff) {
                        return true;
                    }
                }
                break;
        }
        return ret;
    }

}
