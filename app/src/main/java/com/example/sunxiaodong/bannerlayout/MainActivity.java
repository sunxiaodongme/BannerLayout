package com.example.sunxiaodong.bannerlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.example.sunxiaodong.bannerlayout.view.BannerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private BannerLayout mBannerLayout;
    private NumAdapter mNumAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.header_banner_layout, null);
        mBannerLayout = (BannerLayout) headView.findViewById(R.id.banner_layout);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.addHeaderView(headView);
        List<Integer> numList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            numList.add(i);
        }
        mNumAdapter = new NumAdapter(numList);
        mListView.setAdapter(mNumAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setDistanceToTriggerSync(300);//触发刷新的下拉距离
    }

    private void initData() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 1; i <= 5; i++) {
            Fragment fragment = MyFragment.newInstance(i);
            fragments.add(fragment);
        }
        mBannerLayout.setViews(getSupportFragmentManager(), fragments);
    }

}
