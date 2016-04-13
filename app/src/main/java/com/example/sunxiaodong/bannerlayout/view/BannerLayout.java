package com.example.sunxiaodong.bannerlayout.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.sunxiaodong.bannerlayout.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxiaodong on 16/4/8.
 */
public class BannerLayout extends RelativeLayout {

    private static final String TAG = BannerLayout.class.getSimpleName();
    private static final String SXD = "sxd";

    private static final int VIEWPAGER_ENHANCE = 100;//为了做到Banner循环，对数据项进行扩展的扩展比例

    private static final int AUTH_PLAY_INTERVAL = 100;

    private ViewPager viewPager;
    private LinearLayout indicatorContainer;

    private boolean isAutoPlay = true;
    private int autoPlayDuration = 4000;
    private int scrollDuration = 1000;

    private Position indicatorPosition = Position.centerBottom;

    private int selectedIndicatorColor = 0xffff0000;
    private int unSelectedIndicatorColor = 0x88888888;
    private int selectedIndicatorRadius = 6;
    private int unSelectedIndicatorRadius = 6;

    private int indicatorGap = 3;
    private int indicatorMarginTop = 0;
    private int indicatorMarginBottom = 0;
    private int indicatorMarginLeft = 0;
    private int indicatorMarginRight = 0;

    private Drawable unSelectedDrawable;
    private Drawable selectedDrawable;

    private int itemCount;

    private enum Position {
        centerBottom,
        rightBottom,
        leftBottom,
        centerTop,
        rightTop,
        leftTop
    }

    public BannerLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BannerLayout, defStyleAttr, 0);
        selectedIndicatorColor = array.getColor(R.styleable.BannerLayout_selectedIndicatorColor, selectedIndicatorColor);
        unSelectedIndicatorColor = array.getColor(R.styleable.BannerLayout_unSelectedIndicatorColor, unSelectedIndicatorColor);
        selectedIndicatorRadius = (int) array.getDimension(R.styleable.BannerLayout_selectedIndicatorRadius, selectedIndicatorRadius);
        unSelectedIndicatorRadius = (int) array.getDimension(R.styleable.BannerLayout_unSelectedIndicatorRadius, unSelectedIndicatorRadius);

        int position = array.getInt(R.styleable.BannerLayout_indicatorPosition, Position.centerBottom.ordinal());
        for (Position position1 : Position.values()) {
            if (position == position1.ordinal()) {
                indicatorPosition = position1;
            }
        }

        indicatorGap = (int) array.getDimension(R.styleable.BannerLayout_indicatorGap, indicatorGap);
        indicatorMarginTop = (int) array.getDimension(R.styleable.BannerLayout_indicatorMarginTop, indicatorMarginTop);
        indicatorMarginBottom = (int) array.getDimension(R.styleable.BannerLayout_indicatorMarginBottom, indicatorMarginBottom);
        indicatorMarginLeft = (int) array.getDimension(R.styleable.BannerLayout_indicatorMarginLeft, indicatorMarginLeft);
        indicatorMarginRight = (int) array.getDimension(R.styleable.BannerLayout_indicatorMarginRight, indicatorMarginRight);

        isAutoPlay = array.getBoolean(R.styleable.BannerLayout_isAutoPlay, isAutoPlay);
        autoPlayDuration = array.getInt(R.styleable.BannerLayout_autoPlayDuration, autoPlayDuration);
        scrollDuration = array.getInt(R.styleable.BannerLayout_scrollDuration, scrollDuration);

        array.recycle();
        initIndicator();
    }

    private void initIndicator() {
        //绘制未选中状态图形
        LayerDrawable unSelectedLayerDrawable;
        GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
        unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
        unSelectedGradientDrawable.setColor(unSelectedIndicatorColor);
        unSelectedGradientDrawable.setSize(2 * unSelectedIndicatorRadius, 2 * unSelectedIndicatorRadius);
        unSelectedLayerDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        unSelectedDrawable = unSelectedLayerDrawable;
        //绘制选中状态图形
        LayerDrawable selectedLayerDrawable;
        GradientDrawable selectedGradientDrawable = new GradientDrawable();
        selectedGradientDrawable.setShape(GradientDrawable.OVAL);
        selectedGradientDrawable.setColor(selectedIndicatorColor);
        selectedGradientDrawable.setSize(2 * selectedIndicatorRadius, 2 * selectedIndicatorRadius);
        selectedLayerDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        selectedDrawable = selectedLayerDrawable;
    }

    public void setViews(FragmentManager fm, List<Fragment> fragmentList) {
        List<Fragment> newFragmentList = new ArrayList<>();
        itemCount = fragmentList.size();
        switch (itemCount) {
            case 0:
                throw new IllegalStateException("item count not equal zero");
            case 1:
                newFragmentList.add(fragmentList.get(0));
                break;
            default:
                for (int i = 0; i < VIEWPAGER_ENHANCE * itemCount; i++) {
                    newFragmentList.add(fragmentList.get(i % itemCount));
                }
                break;
        }
        initViews(fm, newFragmentList);
    }

    private void initViews(FragmentManager fm, List<Fragment> fragmentList) {
        //初始化pager
        viewPager = new ViewPager(getContext());
        viewPager.setId(R.id.banner_view_pager_id);
        //添加viewpager到SliderLayout
        addView(viewPager);
        setSliderTransformDuration(scrollDuration);

        LoopFragmentPagerAdapter pagerAdapter = new LoopFragmentPagerAdapter(fm, fragmentList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        //设置当前item到Integer.MAX_VALUE中间的一个值，看起来像无论是往前滑还是往后滑都是ok的
        //如果不设置，用户往左边滑动的时候已经划不动了
        int targetItemPosition = pagerAdapter.getCount() / 2 - pagerAdapter.getCount() / 2 % itemCount;
        viewPager.setCurrentItem(targetItemPosition);
        if (fragmentList.size() > 1) {
            initIndicatorContainer();
            switchIndicator(targetItemPosition % itemCount);
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    switchIndicator(position % itemCount);
                }
            });
            startAutoPlay();
        }
    }

    private void initIndicatorContainer() {
        //初始化indicatorContainer
        indicatorContainer = new LinearLayout(getContext());
        indicatorContainer.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (indicatorPosition) {
            case centerBottom:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case centerTop:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case leftBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case leftTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case rightBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case rightTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }
        //设置margin
        params.setMargins(indicatorMarginLeft, indicatorMarginTop, indicatorMarginRight, indicatorMarginBottom);
        //添加指示器容器布局到SliderLayout
        addView(indicatorContainer, params);

        //初始化指示器，并添加到指示器容器布局
        for (int i = 0; i < itemCount; i++) {
            ImageView indicator = new ImageView(getContext());
            indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            indicator.setPadding(0, 0, indicatorGap, 0);
            indicator.setImageDrawable(unSelectedDrawable);
            indicatorContainer.addView(indicator);
        }
    }

    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), null, duration);
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 切换指示器状态
     *
     * @param currentPosition 当前位置
     */
    private void switchIndicator(int currentPosition) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ((ImageView) indicatorContainer.getChildAt(i)).setImageDrawable(i == currentPosition ? selectedDrawable : unSelectedDrawable);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == AUTH_PLAY_INTERVAL) {
                if (viewPager != null) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    handler.sendEmptyMessageDelayed(AUTH_PLAY_INTERVAL, autoPlayDuration);
                }
            }
            return false;
        }
    });

    /**
     * 开始自动轮播
     */
    public void startAutoPlay() {
        stopAutoPlay(); // 避免重复消息
        if (isAutoPlay && viewPager.getAdapter().getCount() > 1) {
            handler.sendEmptyMessageDelayed(AUTH_PLAY_INTERVAL, autoPlayDuration);
        }
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoPlay() {
        if (isAutoPlay) {
            handler.removeMessages(AUTH_PLAY_INTERVAL);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ViewPager滚动器滚动时间处理
     */
    public class FixedSpeedScroller extends Scroller {

        private int duration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            this.duration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, this.duration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, this.duration);
        }
    }

    /**
     * Banner页滚动适配器
     */
    public class LoopFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public LoopFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList.clear();
            this.fragmentList.addAll(fragmentList);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

    }

}
