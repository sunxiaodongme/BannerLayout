package com.example.sunxiaodong.bannerlayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxiaodong on 16/4/12.
 */
public class NumAdapter extends BaseAdapter {

    private List<Integer> mNumList = new ArrayList<>();

    public NumAdapter(List<Integer> numList) {
        mNumList = numList;
    }

    @Override
    public int getCount() {
        return mNumList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.num_adapter_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int num = mNumList.get(position);
        viewHolder.mTextView.setText(num + "");
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
    }

}