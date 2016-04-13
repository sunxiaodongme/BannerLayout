package com.example.sunxiaodong.bannerlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sunxiaodong on 16/1/27.
 */
public class MyFragment extends Fragment {

    private static final String NAME = MyFragment.class.getSimpleName();
    private static final String TAG = "sxd";

    private static final String INDEX = "index";

    private View mRootView;
    private TextView mPage;

    private int mIndex;

    public static MyFragment newInstance(int index) {
        MyFragment myFragment = new MyFragment();
        Bundle argBundle = new Bundle();
        argBundle.putInt(INDEX, index);
        myFragment.setArguments(argBundle);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments().getInt(INDEX);
        Log.i(TAG, NAME + mIndex + "--onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_layout, container, false);
        initView();
        Log.i(TAG, NAME + mIndex + "--onCreateView");
        return mRootView;
    }

    private void initView() {
        mPage = (TextView) mRootView.findViewById(R.id.page);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击" + mIndex, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, NAME + mIndex + "--onActivityCreated");
        initData();
    }

    private void initData() {
        switch (mIndex) {
            case 1:
                mRootView.setBackgroundResource(R.color.color_ff0000);
                break;
            case 2:
                mRootView.setBackgroundResource(R.color.color_ff00ff);
                break;
            case 3:
                mRootView.setBackgroundResource(R.color.color_00ff00);
                break;
            case 4:
                mRootView.setBackgroundResource(R.color.color_ffff00);
                break;
            case 5:
                mRootView.setBackgroundResource(R.color.color_8e35ef);
                break;
        }
        mPage.setText("page" + mIndex);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, NAME + mIndex + "--onHiddenChanged++hidden:" + hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, NAME + mIndex + "--onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, NAME + mIndex + "--onDestroy");
    }
}
