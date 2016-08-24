package com.jeldrik.mytoystechtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jeldrik on 24/08/16.
 */
public class MenuAdapter extends BaseAdapter {

    private ArrayList mData = new ArrayList();
    private LayoutInflater mInflater;
    private final Context context;

    public MenuAdapter(Context context){
        this.context=context;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int i) {
        return (String)mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View rowView=mInflater.inflate(R.layout.section_item,viewGroup,false);
        TextView title=(TextView)rowView.findViewById(R.id.sectionTitle);

        String text=(String)mData.get(position);
        title.setText(text);

        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
