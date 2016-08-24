package com.jeldrik.mytoystechtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jeldrik on 24/08/16.
 */
public class MenuAdapter extends BaseAdapter {

    private ArrayList<JSONObject> mData = new ArrayList();
    private LayoutInflater mInflater;
    private final Context context;

    public MenuAdapter(Context context){
        this.context=context;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void addItem(final JSONObject item) {
        mData.add(item);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public JSONObject getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rowView=view;

        try {
            if(mData.get(position).getString("type").equals("section")){
                rowView=mInflater.inflate(R.layout.section_item,viewGroup,false);
                TextView title=(TextView)rowView.findViewById(R.id.sectionTitle);
                title.setText(mData.get(position).getString("label"));
            }
            else if(mData.get(position).getString("type").equals("node")){
                rowView=mInflater.inflate(R.layout.node_item,viewGroup,false);
                TextView title=(TextView)rowView.findViewById(R.id.nodeTitle);
                title.setText(mData.get(position).getString("label"));
            }
            else if(mData.get(position).getString("type").equals("link")) {
                rowView = mInflater.inflate(R.layout.link_item, viewGroup, false);
                TextView title = (TextView) rowView.findViewById(R.id.linkTitle);
                title.setText(mData.get(position).getString("label"));
            }

        } catch (JSONException e) {
            Log.e("MenuAdapter", "error in MenuAdapter" );
            e.printStackTrace();
        }


        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
